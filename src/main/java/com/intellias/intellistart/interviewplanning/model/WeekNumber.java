package com.intellias.intellistart.interviewplanning.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class WeekNumber {
    Integer weekNum;

    public WeekNumber(Integer weekNum) {
        this.weekNum = weekNum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WeekNumber that = (WeekNumber) o;
        return Objects.equals(weekNum, that.weekNum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(weekNum);
    }
}
