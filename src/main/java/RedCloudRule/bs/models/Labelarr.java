package RedCloudRule.bs.models;

import java.util.List;
import java.util.ArrayList;

public class Labelarr {
    ArrayList<List<Label>> labelbatch;

    public Labelarr(){
        labelbatch = new ArrayList<List<Label>>();
    }

    public Labelarr(ArrayList<List<Label>> label_batch){
        labelbatch = label_batch;
    }

    public void pushnew(List<Label> new_label){
        labelbatch.add(new_label);
    }

    public ArrayList<List<Label>> getlabelbatch(){
        return labelbatch;
    }
}
