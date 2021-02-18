package submarine;

/**
 * This is simple 2D game, the sub marine killer game.
 * The game has a boat around the top of the canvas with a red bomb attached to 
 * it. At the bottom of the canvas is a submarine. The user tries to blow up the 
 * sub by dropping the bomb. The user can drop the bomb by pressing the down 
 * arrow key. The user can also move the boat using the right and the left arrow
 * keys.
 * @author wakura
 */
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.animation.AnimationTimer;
import javafx.scene.paint.Color;
import javafx.scene.control.Button;
public class MySubKiller extends Application{
    public static void main(String[] args){
            launch(args);
    }
    
    //............instance variables declaration...................................................
    private Canvas canvas;                  //The canvas on which drawing is done
    private GraphicsContext g;              //The GraphicsContext object used for drawing in the program
    private Boat boat;                      //The boat object in the program
    private Submarine sub;                        //The submarine object in the program
    private Bomb bomb;                       //The bomb object in the program
    
    public void start(Stage stage){
        canvas = new Canvas(640, 500);
       
        boat = new Boat();
        bomb = new Bomb();
        sub = new Submarine();
        draw();
        Button btn = new Button("New");
        btn.setDisable(true);
        btn.disableProperty().addListener(e -> draw());
        
        
        Pane root = new Pane(canvas);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("SubKiller!");
        stage.setResizable(false);
        stage.show();  
        
        AnimationTimer anim = new AnimationTimer(){
            public void handle(long time){
                boat.updateForNextFrame();
                bomb.updateForNextFrame();
                sub.updateForNextFrame();
                draw();
                
              
            }
        };
                anim.start();
                scene.setOnKeyPressed(this::keyHandler);
    }
    
    private void keyHandler(KeyEvent evt){
        if(evt.getCode() == KeyCode.DOWN)
            bomb.isFalling = true;      //release the bomb
        else if(evt.getCode() == KeyCode.LEFT)
            boat.centerX -= 15;         //move the boat left
        else if(evt.getCode() == KeyCode.RIGHT)
            boat.centerX += 15;         //move the boat right
    }
    
    private class Boat{
        double centerX;            //The x- and y- coordinates of the center of the boat
        final double centerY;
        
        //constructor
        public Boat(){
            centerX = canvas.getWidth()/2;
            centerY = 60;
        }
        
        void updateForNextFrame(){
            if((centerX - 15) < 0)
                centerX = 0;
            if((centerX + 15) > canvas.getWidth())
                centerX = canvas.getWidth();
        }
        
        void draw(GraphicsContext g){
            g.setFill(Color.BLUE);
            g.fillRoundRect(centerX - 30, centerY - 30, 60, 60, 5, 5);
        }
    }
    
    private class Bomb{
        double centerX, centerY;            //The x- and y- coordinates of the center of the bomb
        boolean isFalling;                  //True if the user has released the bomb and is falling
        
        //constructor
        public Bomb(){//construct the bomb initially attached to the boat
            centerX = boat.centerX;
            centerY = boat.centerY + 30;
        }
        
        void updateForNextFrame(){
            
            
            
            if(isFalling)       //The bomb is falling
                centerY += 15; 
            else{
                centerX = boat.centerX;
                centerY = boat.centerY +30;
            }

            
            //check if the bomb hit the sub or has fallen off the screen
            if(centerY >= canvas.getWidth()){   //The bomb has missed the sub and fallen off the screen
                centerY = boat.centerY + 30;
                centerX = boat.centerX;
                isFalling = false;
            }
            else if ((Math.abs(centerX - sub.centerX) <= 30) && (Math.abs(centerY -sub.centerY) <= 10)){    //The bomb has hit the sub and is exploding
                sub.isExploding = true;
                sub.explosionFrameNumber = 1;
                isFalling = false;
            }           
        }
        
        void draw(GraphicsContext g){
            g.setFill(Color.RED);
            g.fillOval(centerX - 8, centerY - 8, 15, 15);
        }
    }
    
    private class Submarine {
        double centerX ;    //the coordinates of the center of the sub
        final double centerY;
        boolean isMovingLeft;       //true if the sub is moving to the left
        int explosionFrameNumber;   //used to animate the explosion of the submarine
        boolean isExploding;        //true if the submarine is exploding
        
        //constructor
        public Submarine(){
            centerX = canvas.getWidth()*Math.random();
            centerY = canvas.getHeight()-40;
            isMovingLeft = Math.random() < 0.5;
        }
        
        void updateForNextFrame(){
            
            if(Math.random() < 0.02){
                isMovingLeft = !isMovingLeft;
            }
            if(isMovingLeft){
                centerX -= 5;
                if (centerX < 0){
                    centerX = 0;
                    isMovingLeft = false;
                }
            }
            else {
                centerX += 5;
                if(centerX > canvas.getWidth()){ 
                    centerX = canvas.getWidth();
                    isMovingLeft = true;
                }
            }
            if(isExploding){
                if(explosionFrameNumber == 45){
                    isExploding = false;
                    centerX = canvas.getWidth()*Math.random();
                }
                
            }
        }
        
        void draw(GraphicsContext g){
            if (isExploding){
                explosionFrameNumber++;
                g.setFill(Color.VIOLET);
                g.fillOval(centerX - 3*explosionFrameNumber, centerY - 1.5*explosionFrameNumber, 6*explosionFrameNumber, explosionFrameNumber*3);
                g.setFill(Color.RED);
                g.fillOval(centerX - 1.5*explosionFrameNumber, centerY - explosionFrameNumber/2, 3*explosionFrameNumber, explosionFrameNumber);
            }
            else {
//                centerX = canvas.getWidth()*Math.random();
                g.setFill(Color.BLACK);
                g.fillOval(centerX - 30, centerY - 10, 60, 20);
            }
        }
    }
    
    private void draw(){
        g = canvas.getGraphicsContext2D();
        g.setFill(Color.LIGHTGREEN);
        g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        
        boat.draw(g);
        bomb.draw(g);
        sub.draw(g);
    }
   

}
