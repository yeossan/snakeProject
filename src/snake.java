import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.Random;
import java.util.LinkedList;
public class snake extends Thread{
    static class MyFrame extends JFrame {
        static class XY {
            int x;
            int y;
            public XY(int x, int y) {
                this.x = x;
                this.y = y;
            }
        }
        JPanel panelNorth; //점수, 시간을 표시
        JPanel panelCenter; //뱀의 이동경로 표시
        JLabel labelTitle;
        JLabel labelMessage;
        JPanel[][] panels = new JPanel[20][20]; //20x20의 이차원배열
        int[][] map = new int[20][20]; //과일의 위치.
        LinkedList<XY> snake = new LinkedList<XY>();
        int dir = 3; //진행방향 0:위, 1:아래, 2:왼쪽, 3:오른쪽
        int score = 0; //점수 0
        int time = 0; //시간 0
        int timeCount = 0;
        Timer timer = null;


        public MyFrame() {
            super("snake game");
            setSize(400, 500);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setVisible(true);
            buildGUI(); //레이블을 부착하는 메서드
            makeSnakeList(); //뱀의 머리, 몸통, 꼬리를 만드는 메서드
            startTimer(); //시간을 카운트 하는 메서드
            setKeyListener(); //키보드 방향키가 눌러졌을때 실행되는 메서드
            makeFruit(); //과일을 생성하는 메서드
        }


        private void makeFruit() {
            Random rand = new Random(); //x:0~19, y:0~19 사이에 렌덤으로 과일배치
            int randX = rand.nextInt(19);
            int randY = rand.nextInt(19);
            map[randX][randY] = 9; //9는 과일
        }


        private void setKeyListener() {
            this.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) { //키보드가 눌렸을때 이벤트 발생
                    if(e.getKeyCode() == KeyEvent.VK_UP) {
                        if(dir != 1) //뱀이 정반대로 가면 안됨
                            dir = 0;
                    }
                    else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
                        if(dir != 0)
                            dir = 1;
                    }
                    else if(e.getKeyCode() == KeyEvent.VK_LEFT) {
                        if(dir != 3)
                            dir = 2;
                    }
                    else if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
                        if(dir != 2)
                            dir = 3;
                    }
                }
            });
        }


        private void startTimer() {
            timer = new Timer(200, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) { //1초에 200ms
                    timeCount += 1;
                    if(timeCount % 10 == 0) { //1초가 되었을때
                        time++; //1초 증가
                    }
                    moveSnake(); //뱀을 움직이는 메서드
                    updateUI();
                }
            });
            timer.start(); //시작
        }
        private void moveSnake() { //뱀을 움직이는 메서드(진행방향의 반대로는 못감)
            //초깃값은 오른쪽으로 설정
            MyFrame.XY headXY = snake.get(0);
            int headX = headXY.x;
            int headY = headXY.y;

            if(dir == 0) { //0:위로, 1:아래로, 2:왼쪽으로, 3:오른쪽으로
                boolean isColl = checkCollision(headX, headY-1);
                if(isColl == true) { //머리와 꼬리가 겹친경우
                    try {
                        Thread.sleep(20);
                        labelMessage.setText("game over"); //머리와 꼬리가 겹친경우 2초뒤  game over 출력
                    }catch(InterruptedException e) {
                        e.printStackTrace();
                    }

                    timer.stop();
                    return;
                }
                snake.add(0, new XY(headX, headY-1)); //뱀머리 한칸 추가
                snake.remove(snake.size() -1); //뱀 꼬리 한칸 삭제, 뱀이 이동하는것 처럼 보임
            }
            else if(dir == 1) {
                boolean isColl = checkCollision(headX, headY+1);
                if(isColl == true) {
                    try {
                        Thread.sleep(20);
                        labelMessage.setText("game over");
                    }catch(InterruptedException e) {
                        e.printStackTrace();
                    }

                    timer.stop();
                    return;
                }
                snake.add(0, new XY(headX, headY+1)); //y좌표 한칸올려서 위로가기
                snake.remove(snake.size() -1); //뱀 꼬리 한칸 삭제
            }
            else if(dir == 2) {
                boolean isColl = checkCollision(headX-1, headY);
                if(isColl == true) {
                    try {
                        Thread.sleep(20);
                        labelMessage.setText("game over");
                    }catch(InterruptedException e) {
                        e.printStackTrace();
                    }

                    timer.stop();
                    return;
                }
                snake.add(0, new XY(headX-1, headY)); //x좌표 한칸 빼서 왼쪽으로 가기
                snake.remove(snake.size() -1);//뱀 꼬리 한칸 삭제
            }
            else if(dir == 3) {
                boolean isColl = checkCollision(headX+1, headY);
                if(isColl == true) {
                    try {
                        Thread.sleep(50);
                    }catch(InterruptedException e) {
                        e.printStackTrace();
                    }

                    timer.stop();
                    return;
                }
                snake.add(0, new XY(headX+1, headY)); //x좌표 한칸 더해서 오른쪽으로 가기
                snake.remove(snake.size() -1); //뱀 꼬리 한칸 삭제
            }
        }
        public boolean checkCollision(int headX, int headY) {
            if(headX<0 || headX>19 || headY<0 || headY>19) { //벽에 충돌(닿았을때, 타일이 20x20이라 19로 설정)
                return true;
            }
            //뱀의 몸끼리 충돌시 game over
            for(XY xy : snake) {
                if(headX == xy.x && headY == xy.y) {
                    return true; //game over
                }
            }
            if(map[headY][headX] == 9) { //뱀이 과일에 충돌(닿았을때)
                map[headY][headX] = 0; //열은 행으로, 행은 열로
                addTail();
                makeFruit();
                score += 10; //과일에 닿으면 10점 추가
            }
            return false;
        }

        private void addTail() { //뱀의 길이를 늘리는 메서드
            int tailX = snake.get(snake.size()-1).x;
            int tailY = snake.get(snake.size()-1).y;
            int tailX1 = snake.get(snake.size()-2).x;
            int tailY1 = snake.get(snake.size()-2).y;
            if(tailX < tailX1) { //진행방향 오른쪽인 경우: 왼쪽에 붙히기
                snake.add(new XY(tailX-1, tailY));
            }
            else if(tailX > tailX1) { //진행방향 왼쪽인 경우: 오른쪽에 붙히기
                snake.add(new XY(tailX+1, tailY));
            }
            else if(tailY < tailY1) { //진행방향 위쪽인 경우: 아래쪽에 붙히기
                snake.add(new XY(tailX, tailY-1));
            }
            else if(tailY > tailY1) { //진행방향 아래쪽인 경우: 위쪽에 붙히기
                snake.add(new XY(tailX-1, tailY+1));
            }
        }


        private void updateUI() {
            labelTitle.setText("score: " +score+ "Time: " +time);

            //clear tile(panel)
            for(int i=0; i<20; i++) {
                for(int j=0; j<20; j++) {
                    if(map[i][j] == 0) { //아무것도 없는칸
                        panels[i][j].setBackground(Color.GRAY);
                    }
                    else if(map[i][j] == 9) { //과일이 있는칸
                        panels[i][j].setBackground(Color.BLUE);
                    }
                }
            }
            //뱀 그리기
            int i = 0;
            for(XY xy : snake) {
                if(i == 0) { //머리
                    panels[xy.y][xy.x].setBackground(Color.YELLOW);
                }else { //몸통
                    panels[xy.y][xy.x].setBackground(Color.GREEN);
                }
                i++;
            }
        }

        private void makeSnakeList() {
            snake.add(new XY(10,10)); //뱀의 머리위치
            snake.add(new XY(9,10)); //뱀의 몸통위치
            snake.add(new XY(8,10)); //뱀의 꼬리위치
        }


        private void buildGUI() {
            this.setLayout(new BorderLayout());
            panelNorth = new JPanel();
            panelNorth.setPreferredSize(new Dimension(400,100));
            panelNorth.setBackground(Color.MAGENTA);
            panelNorth.setLayout(new FlowLayout());

            labelTitle = new JLabel("Score: 0, Time: 0sec");
            labelTitle.setPreferredSize(new Dimension(400,50));
            labelTitle.setForeground(Color.WHITE);
            labelTitle.setHorizontalAlignment(JLabel.CENTER);
            panelNorth.add(labelTitle);

            labelMessage = new JLabel("과일먹기");
            labelMessage.setPreferredSize(new Dimension(400,20));
            labelMessage.setForeground(Color.YELLOW);
            labelMessage.setHorizontalAlignment(JLabel.CENTER);
            panelNorth.add(labelMessage);

            this.add("North", panelNorth);

            panelCenter = new JPanel();
            panelCenter.setLayout(new GridLayout(20,20)); //20x20
            for(int i=0; i<20; i++) { //열
                for(int j=0; j<20; j++) { //행, 이중for문으로 바둑판처럼만들기
                    map[i][j] = 0;
                    panels[i][j] = new JPanel();
                    panels[i][j].setPreferredSize(new Dimension(20,20));
                    panels[i][j].setBackground(Color.GRAY);
                    panelCenter.add(panels[i][j]);

                }
            }
            this.add("Center", panelCenter);
            this.pack(); //빈공간 없애주기

        }

    }
    public static void main(String[] args) {
        new MyFrame();
    }
}