package com.example.android.volleyball;


import com.google.gson.annotations.SerializedName;

public class Programmer {
    private String name;
    private String surname;
    @SerializedName("skill_level")
    private int skillLevel;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public int getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(int skillLevel) {
        this.skillLevel = skillLevel;
    }

    @Override
    public String toString() {
        return "I am "+getName()+" "+getSurname()+". My skill level is "+getSkillLevel()+".";
    }
}
