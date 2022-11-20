package com.ssafy.catchmind.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Point {
    @Override
    public String toString() {
        return "CanvasPoint{" +
                "x=" + x +
                ", y=" + y +
                ", isContinue=" + isContinue +
                ", color=" + color +
                ", width=" + width +
                '}';
    }

    Float x;
    Float y;
    Boolean isContinue;
    Integer color;
    Float width;

}
