package eda;

public class ICBernouilli {
	
	private double p;
	private double a, b;
	
	public ICBernouilli(double p){
            this.p = p;
	}
	
	
	public ICBernouilli(double p, int a, int b){
            this.p = p;
            this.a = a;
            this.b = b;
	}
	
	public void setP(double p){
            this.p = p;
	}
	
        public double getP() {
            return this.p;
        }
        
	public int newSample(){
            double x;
            int value;

            x = Math.random();
            if(x < p)   value = 0;
            else        value = 1;
            return value;
	}
}
