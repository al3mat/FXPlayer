    package sample;

    import javafx.collections.FXCollections;
    import javafx.collections.ObservableList;
    import javafx.scene.control.ListView;

    import java.io.File;
    import java.util.ArrayList;
    import java.util.List;



    class listTitle
    {
        private ListView lv = new ListView();
        ObservableList<String> st;
        private List<String> nameList = new ArrayList<>();

        void takeTitles(List<String> names)
        {
            File f;

            for (int i = 0; i < names.size(); i++){
                f = new File(names.get(i));
                nameList.add(i, f.getName());
            }

            st = FXCollections.observableArrayList(nameList);
            clearNameList();
            lv.setItems(st);
        }

        void clearNameList(){
            nameList.clear();
        }
    }
