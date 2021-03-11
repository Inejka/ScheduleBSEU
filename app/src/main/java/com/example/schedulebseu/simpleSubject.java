package com.example.schedulebseu;

import java.io.Serializable;

public class simpleSubject implements Serializable {
    private static final long serialVersionUID = 4L;
    public String time = "";
    public String subjectName = "";
    public String type = "";
    public String lecturer = "";
    public String classroom = "";
    public String customInfo = "";

    simpleSubject copy(){
        simpleSubject toReturn = new simpleSubject();
        toReturn.classroom=this.classroom;
        toReturn.time=this.time;
        toReturn.subjectName=this.subjectName;
        toReturn.lecturer=this.lecturer;
        toReturn.type=this.type;
        return toReturn;
    }
}
