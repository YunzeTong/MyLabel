package RedCloudRule.bs.models;

public class LabelInfo {
    private double vx;
    private double vy;
    private int width;
    private int height;

    protected LabelInfo(){}
    public LabelInfo(double v_x, double v_y, int width, int height){
        vx = v_x;
        vy = v_y;
        this.width = width;
        this.height = height;
    }

    public double getVx(){return vx;}
    public double getVy(){return vy;}
    public int getWidth(){return width;}
    public int getHeight(){return height;}
}
