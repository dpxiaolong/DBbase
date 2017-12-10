package DBframe.Bean;

/**测试用javabean
 * Created by luozhenlong on 2017/12/9.
 */

public class Student {
    private String name;
    private int age;
    private boolean isMale;
    private float salary;

    public Student() {
    }

    public Student(String name, int age, boolean isMale, float salary) {
        this.name = name;
        this.age = age;
        this.isMale = isMale;
        this.salary = salary;
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
        return "Student{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", isMale=" + isMale +
                ", salary=" + salary +
                '}';
    }
}
