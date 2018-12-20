package com.cacha.expressinquiry.entities;

public class TypeEntity {
    public int id;
    public String typeName;


    public TypeEntity() {
    }

    public TypeEntity(int id, String typeName) {
        this.id = id;
        this.typeName = typeName;
    }

    @Override
    public String toString() {
        return typeName;
    }
}

