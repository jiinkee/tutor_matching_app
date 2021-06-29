package com.edu.monash.fit3077.view;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;

public interface LessonDayTimeSetListener {
    void onLessonDayTimeSet(HashMap<String, ArrayList<LocalTime[]>> selectedLessonDayTime);
}
