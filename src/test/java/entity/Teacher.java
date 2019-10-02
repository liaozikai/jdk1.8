package test.java.entity;


/**
 * @author liaozikai
 * @description: 测试Object对象的clone方法
 * @date 2019/9/2222:30
 */
public class Teacher implements Cloneable{
    private String name;
    private Student student;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    @Override
    public String toString() {
        return "Teacher [name=" + name + ", student=" + student.getName() + "]";
    }

    /*teacher3:Teacher [name=小赵老师, student=李四]
    teacher4:Teacher [name=小明老师, student=李四]
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }*/

    @Override
    protected Object clone() throws CloneNotSupportedException {
        Teacher teacher = (Teacher) super.clone();
        if(null != teacher.getStudent()) {
            teacher.setStudent((Student) teacher.getStudent().clone());
        }
        return teacher;
    }

    public static void main(String[] args) throws Exception{

        Teacher teacher1 = new Teacher();
        Teacher teacher2 = (Teacher) teacher1.clone();
        System.out.println(teacher1 == teacher2); // false
        System.out.println(teacher1.getClass() == teacher2.getClass()); // true
        System.out.println(teacher1.equals(teacher2)); // false


      /*  teacher3:Teacher [name=小赵老师, student=张三]
        teacher4:Teacher [name=小明老师, student=李四]*/
        Student s1 = new Student();
        s1.setName("张三");
        Teacher teacher3 = new Teacher();
        teacher3.setName("小赵老师");
        teacher3.setStudent(s1);
        Teacher teacher4 = (Teacher)teacher3.clone();
        teacher4.setName("小明老师");
        Student s2 = teacher4.getStudent();
        s2.setName("李四");
        System.out.println("teacher3:"+teacher3);
        System.out.println("teacher4:"+teacher4);

    }
}
