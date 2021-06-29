package com.edu.monash.fit3077.viewAdapter;

import android.content.Context;
import androidx.annotation.NonNull;
import com.edu.monash.fit3077.R;
import java.util.ArrayList;

/**
 * Wrapper class for array adapter with generic type
 * @param <T>
 */
public class BaseArrayAdapter<T> extends android.widget.ArrayAdapter {

    private ArrayList<T> options;

    /**
     * Instantiate BaseArrayAdapter
     * @param context current context
     * @param options list of options object for adapter
     */
    public BaseArrayAdapter(@NonNull Context context, @NonNull ArrayList<T> options) {
        super(context, R.layout.layout_spinner_item, options==null? new ArrayList(): options);
        this.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.options = options==null? new ArrayList<>(): options;
    }

    /**
     * Setter for options list in adapter
     * @param newOptions list of options to be updated to adapter
     */
    public void setOptions(ArrayList<T> newOptions) {
        clear();
        addAll(newOptions);
    }
}
