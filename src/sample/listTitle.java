package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import java.util.List;



public class listTitle
{
    ListView lv = new ListView();
    ObservableList<String> st;

    void takeTitles(List<String> names)
    {
        st = FXCollections.observableArrayList(names);

        lv.setItems(st);
    }
}
