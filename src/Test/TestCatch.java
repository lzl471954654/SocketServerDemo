package Test;

public class TestCatch {
    public static void main(String[] args) {
        try{
            String s = null;
            s.toString();
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }finally {
            System.out.println("I am finally block!");
        }
    }
}
