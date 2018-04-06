package sample;

import javafx.beans.Observable;
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

        for(int i = 0; i < st.size(); i++)
            System.out.println("in listView\t"+st.get(i));

        lv.setItems(st);
    }

    int isClicked()
    {
        return lv.getSelectionModel().getSelectedIndex();
    }

}
