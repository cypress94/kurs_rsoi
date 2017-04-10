package ru.belyaeva.rsoi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by user on 29.10.2016.
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) throws Exception{
        SpringApplication.run(Application.class);
      /*  Class integer = Class.forName("java.lang.Integer");
        Class inner = Class.forName("java.lang.Integer$IntegerCache");
        Integer j = 5;
        Field[] fields = inner.getDeclaredFields();
        for( int i = 0 ; i < fields.length ; i++ )
        {
            fields[i].setAccessible(true);
            if (fields[i].getName().equals("low")){
                fields[i].set(new Field(),2);
            }
            System.out.println("Field Name-->"+fields[i].getName()+"\t"
                    +"Field Type-->"+ fields[i].getType().getName()+"\t");
        }


        System.out.println(j);
*/
    }
}