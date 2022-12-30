import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class BasicFrame extends JFrame {

    Image image = null;
    JLabel rightLabel = new JLabel();
    JLabel leftLabel = new JLabel();

    JPanel menuPanel;
    JPanel imagePanel;

    JButton uploadBtn;
    JButton grayBtn;
    JButton edgeBtn;
    JButton smoothBtn;
    JSlider brightSlider;

    // 파일 입출력
    FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG, JPEG, PNG", "jpg", "jpeg", "png");
    String filePath;

    BufferedImage leftImage;
    BufferedImage rightImage;


    // flag
    boolean graySelect = false;
    boolean edgeSelect = false;
    boolean smoothSelect = false;

    public BasicFrame()
    {
        setLayout(null);
        setSize(800, 700);

        addMenuPanel();
        addImagePanel();

        setVisible(true);

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        addUploadButton(menuPanel, imagePanel);
        addGrayButton(menuPanel, imagePanel);
        addEdgeButton(menuPanel, imagePanel);
        addSmoothButton(menuPanel, imagePanel);
        addBrightSlider(menuPanel, imagePanel);
    }

    void addMenuPanel()
    {
        menuPanel = new MenuPanel();
        add(menuPanel);
    }

    void addImagePanel()
    {
        imagePanel = new ImagePanel();
        add(imagePanel);
    }

    void addUploadButton(JPanel btnPanel, JPanel showPanel)
    {
        uploadBtn = new JButton("Open");
        btnPanel.add(uploadBtn);

        uploadBtn.setBounds(20, 10, 100, 50);

        uploadBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showPanel.add(rightLabel);
                showPanel.add(leftLabel);

                JFileChooser imageChooser = new JFileChooser();
                imageChooser.setFileFilter(filter);

                int ret = imageChooser.showOpenDialog(null);
                if(ret != JFileChooser.APPROVE_OPTION)
                {
                    JOptionPane.showMessageDialog(null, "파일을 선택하지 않았습니다.", "알림", JOptionPane.WARNING_MESSAGE);
                }

                filePath = imageChooser.getSelectedFile().getPath();

                // 그냥 이미지로 가져오기
//                ImageIcon imgIcon = new ImageIcon(filePath);
//                Image img = imgIcon.getImage();
//                img.getScaledInstance(350, 610, Image.SCALE_SMOOTH);
//                imgIcon = new ImageIcon(img);

//                label.setIcon(imgIcon);
//                label.setBounds(20, 10, 350, 610);


                // BufferedImage로 가져오기
                File imageFile = new File(filePath);
                try {
                    leftImage = ImageIO.read(imageFile);
                    int width = leftImage.getWidth();
                    int height = leftImage.getHeight();
                    graySelect = false;

                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                leftLabel.setIcon(new ImageIcon(leftImage));

                leftLabel.setBounds(20, 10, 350, 610);

                rightImage = new BufferedImage(leftImage.getWidth(), leftImage.getHeight(), Image.SCALE_SMOOTH);

                for(int i = 0; i < leftImage.getWidth() ; i++) {
                    for (int j = 0; j < leftImage.getHeight(); j++)
                    {
                        rightImage.setRGB(i, j, new Color(leftImage.getRGB(i, j)).getRGB());
                    }
                }
                rightLabel.setBounds(400, 10, 350, 610);
                rightLabel.setIcon(new ImageIcon(rightImage));
            }
        });
    }

    void addGrayButton(JPanel btnPanel, JPanel showPanel)
    {
        grayBtn = new JButton("Gray");
        btnPanel.add(grayBtn);

        grayBtn.setBounds(140, 10, 100, 50);

        grayBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BufferedImage bufferedImage;
                if (!graySelect && !smoothSelect) {
                    bufferedImage = getGrayScaleImage(leftImage);
                }
                else if(!graySelect && smoothSelect) {
                    bufferedImage = averageFiltering(getGrayScaleImage(leftImage));
                }
                else if(graySelect && !smoothSelect)
                {
                    bufferedImage = copyBufferedImage(leftImage);
                }
                else
                    bufferedImage = averageFiltering(leftImage);

                graySelect = !graySelect;
                edgeSelect = false;
                rightLabel.setIcon(new ImageIcon(bufferedImage));
            }
        });
    }

    void addEdgeButton(JPanel btnPanel, JPanel showPanel)
    {
        edgeBtn = new JButton("Edge");
        btnPanel.add(edgeBtn);

        edgeBtn.setBounds(260, 10, 100, 50);

        edgeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!edgeSelect) {
                    rightImage = getGrayScaleImage(leftImage);
                    rightImage = averageFiltering(rightImage);
                    rightImage = edgeDetection(rightImage);
                }
                else
                    rightImage = copyBufferedImage(leftImage);

                edgeSelect = !edgeSelect;
                graySelect = false;
                smoothSelect = false;
                rightLabel.setIcon(new ImageIcon(rightImage));
            }
        });
    }

    void addSmoothButton(JPanel btnPanel, JPanel showPanel)
    {
        smoothBtn = new JButton("Smooth");
        btnPanel.add(smoothBtn);

        smoothBtn.setBounds(380, 10, 100, 50);

        smoothBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!smoothSelect && !graySelect)
                {
                    rightImage = averageFiltering(leftImage);
                }
                else if(!smoothSelect)
                {
                    rightImage = averageFiltering(getGrayScaleImage(leftImage));
                }
                else if(!graySelect)
                {
                    rightImage = copyBufferedImage(leftImage);
                }
                else
                    rightImage = getGrayScaleImage(leftImage);

                rightLabel.setIcon(new ImageIcon(rightImage));
                smoothSelect = !smoothSelect;
                edgeSelect = false;
            }
        });
    }

    void addBrightSlider(JPanel btnPanel, JPanel showPanel)
    {
        brightSlider = new JSlider(JSlider.HORIZONTAL, -5, 5, 0);
        btnPanel.add(brightSlider);

        brightSlider.setBounds(500, 10, 300, 50);
        brightSlider.setMajorTickSpacing(1);
//        brightSlider.setMinorTickSpacing(5);
        brightSlider.setPaintTicks(true);
        brightSlider.setInverted(true);

        brightSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider jValue = (JSlider) e.getSource();

                double value = Math.pow(2, jValue.getValue());

                rightImage = gammaTransformation(leftImage, value);
                rightLabel.setIcon(new ImageIcon(rightImage));
            }
        });
    }

    BufferedImage getGrayScaleImage(BufferedImage bImg)
    {
        BufferedImage afterImage;
        afterImage = new BufferedImage(bImg.getWidth(), bImg.getHeight(), Image.SCALE_SMOOTH);
        int[] imgRgb = new int[3];
        int grayNum = 0;

        for(int i = 0 ; i < bImg.getWidth() ; i++)
        {
            for(int j = 0 ; j < bImg.getHeight() ; j++)
            {
                grayNum = 0;
                imgRgb[0] = new Color(bImg.getRGB(i, j)).getRed();
                imgRgb[1] = new Color(bImg.getRGB(i, j)).getGreen();
                imgRgb[2] = new Color(bImg.getRGB(i, j)).getBlue();

                for(int x = 0 ; x < 3 ; x++)
                    grayNum += imgRgb[x];
                grayNum /= 3;
                afterImage.setRGB(i, j, new Color(grayNum, grayNum, grayNum).getRGB());
            }
        }

        return afterImage;
    }

    BufferedImage gammaTransformation(BufferedImage bImg, double gamma)
    {
        BufferedImage afterImage = new BufferedImage(bImg.getWidth(), bImg.getHeight(), Image.SCALE_SMOOTH);
        BufferedImage grayImage = getGrayScaleImage(bImg);

        int[] gammaArray = new int[256];

        for(int i = 0 ; i < 256 ; i++) {
            gammaArray[i] = (int) (Math.pow(i / 255.0, gamma) * 255);
        }

        for(int i = 0 ; i < bImg.getWidth() ; i++)
        {
            for(int j = 0 ; j < bImg.getHeight() ; j++)
            {
                Color gammaColor = new Color(bImg.getRGB(i, j));
                Color grayColor = new Color(grayImage.getRGB(i, j));

                if(!graySelect)
                    afterImage.setRGB(i, j, new Color(gammaArray[gammaColor.getRed()], gammaArray[gammaColor.getGreen()], gammaArray[gammaColor.getBlue()]).getRGB());
                else
                    afterImage.setRGB(i, j, new Color(gammaArray[grayColor.getRed()], gammaArray[grayColor.getGreen()], gammaArray[grayColor.getBlue()]).getRGB());
            }
        }

        return afterImage;
    }

    BufferedImage copyBufferedImage(BufferedImage inputImage)
    {
        BufferedImage outputImage = new BufferedImage(inputImage.getWidth(), inputImage.getHeight(), Image.SCALE_SMOOTH);

        for(int i = 0 ; i < inputImage.getWidth() ; i++) {
            for (int j = 0; j < inputImage.getHeight(); j++) {
                Color inputColor = new Color(inputImage.getRGB(i, j));
                outputImage.setRGB(i, j, new Color(inputColor.getRGB()).getRGB());
            }
        }

        return outputImage;
    }

    BufferedImage averageFiltering(BufferedImage inputImage)
    {
        BufferedImage outputImage = new BufferedImage(inputImage.getWidth(), inputImage.getHeight(), Image.SCALE_SMOOTH);

        Color pixColor;

        for(int i = 0 ; i < inputImage.getWidth() ; i++)
        {
            for(int j = 0 ; j < inputImage.getHeight() ; j++)
            {
                int avgColorR = 0;
                int avgColorG = 0;
                int avgColorB = 0;
                int cnt = 0;

                for(int x = -1 ; x < 2 ; x++) {
                    for (int y = -1; y < 2; y++) {
                        if(i + x < 0 || i + x >= inputImage.getWidth() || j + y < 0 || j + y >= inputImage.getHeight())
                            continue;

                        pixColor = new Color(inputImage.getRGB(i + x, j + y));
                        avgColorR += pixColor.getRed();
                        avgColorG += pixColor.getGreen();
                        avgColorB += pixColor.getBlue();
                        cnt++;
                    }
                }
                avgColorR /= cnt;
                avgColorG /= cnt;
                avgColorB /= cnt;

                outputImage.setRGB(i, j, new Color(avgColorR, avgColorG, avgColorB, Image.SCALE_SMOOTH).getRGB());
            }
        }

        return outputImage;
    }

    BufferedImage edgeDetection(BufferedImage inputImage)
    {
        BufferedImage outputImage = new BufferedImage(inputImage.getWidth(), inputImage.getHeight(), Image.SCALE_SMOOTH);
        int x, y;

        for(int i = 1 ; i < inputImage.getWidth() - 1 ; i++)
        {
            for(int j = 1 ; j < inputImage.getHeight() - 1 ; j++)
            {
                x = 0;
                y = 0;
                Color myColor = new Color(inputImage.getRGB(i - 1, j - 1));

                // x값으로 검출
                x += myColor.getRed() * (-1);
                myColor = new Color(inputImage.getRGB(i - 1, j));
                x += myColor.getRed() * (-2);
                myColor = new Color(inputImage.getRGB(i - 1, j + 1));
                x += myColor.getRed() * (-1);

                myColor = new Color(inputImage.getRGB(i + 1, j - 1));
                x += myColor.getRed();
                myColor = new Color(inputImage.getRGB(i + 1, j));
                x += myColor.getRed() * 2;
                myColor = new Color(inputImage.getRGB(i + 1, j + 1));
                x += myColor.getRed();

                // y값으로 검출
                myColor = new Color(inputImage.getRGB(i - 1, j - 1));
                y += myColor.getRed() * (-1);
                myColor = new Color(inputImage.getRGB(i, j - 1));
                y += myColor.getRed() * (-2);
                myColor = new Color(inputImage.getRGB(i + 1, j - 1));
                y += myColor.getRed() * (-1);

                myColor = new Color(inputImage.getRGB(i - 1, j + 1));
                y += myColor.getRed();
                myColor = new Color(inputImage.getRGB(i, j + 1));
                y += myColor.getRed() * 2;
                myColor = new Color(inputImage.getRGB(i + 1, j + 1));
                y += myColor.getRed();

                int g = (int) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));

                if(g > 130)
                    myColor = new Color(255, 255, 255);
                else
                    myColor = new Color(0, 0, 0);

                outputImage.setRGB(i, j, myColor.getRGB());
            }
        }

        return outputImage;
    }
}
