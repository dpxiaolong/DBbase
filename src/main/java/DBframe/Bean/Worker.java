package DBframe.Bean;

/**测试用javabean
 * Created by luozhenlong on 2017/12/9.
 */

public class Worker {
    private String name;
    private int age;
    private boolean isMale;
    private float salary;
    private String workTime;

    public String getWorkTime() {
        return workTime;
    }

    public void setWorkTime(String workTime) {
        this.workTime = workTime;
    }

    public Worker() {
    }

    public Worker(String name, int age, boolean isMale, float salary,String workTime) {
        this.name = name;
        this.age = age;
        this.isMale = isMale;
        this.salary = salary;
        this.workTime = workTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isMale() {
        return isMale;
    }

    public void setMale(boolean male) {
        isMale = male;
    }

    public float getSalary() {
        return salary;
    }

    public void setSalary(float salary) {
        this.salary = salary;
    }

    @Override
    public String toString() {
        return "Worker{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", isMale=" + isMale +
                ", salary=" + salary +
                ", workTime='" + workTime + '\'' +
                '}';
    }
}
