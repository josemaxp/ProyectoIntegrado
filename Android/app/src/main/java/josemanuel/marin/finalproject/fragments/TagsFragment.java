package josemanuel.marin.finalproject.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import josemanuel.marin.finalproject.MainActivity;
import josemanuel.marin.finalproject.R;

public class TagsFragment extends Fragment {
    AutoCompleteTextView newTag;
    TextView errorText;
    Button addTag, buttonTag1, buttonTag2, buttonTag3, buttonTag4, buttonTag5, buttonTag6, buttonTag7, buttonTag8, buttonTag9, buttonTag10, buttonPopularTag1, buttonPopularTag2, buttonPopularTag3;
    static List<String> tagsList = new ArrayList<>();
    List<Button> buttonsList = new ArrayList<>();
    List<Button> popularButtonsList = new ArrayList<>();
    PrintWriter out = null;
    BufferedReader in = null;
    getPopularTags getPopularTags;
    getTagsName getTagsName;
    getRelationTags getRelationTags;

    public TagsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tags, container, false);

        newTag = view.findViewById(R.id.editTextTextAddTag);
        errorText = view.findViewById(R.id.textViewError);
        addTag = view.findViewById(R.id.buttonAddTag);
        buttonTag1 = view.findViewById(R.id.buttonTag1);
        buttonTag2 = view.findViewById(R.id.buttonTag2);
        buttonTag3 = view.findViewById(R.id.buttonTag3);
        buttonTag4 = view.findViewById(R.id.buttonTag4);
        buttonTag5 = view.findViewById(R.id.buttonTag5);
        buttonTag6 = view.findViewById(R.id.buttonTag6);
        buttonTag7 = view.findViewById(R.id.buttonTag7);
        buttonTag8 = view.findViewById(R.id.buttonTag8);
        buttonTag9 = view.findViewById(R.id.buttonTag9);
        buttonTag10 = view.findViewById(R.id.buttonTag10);
        buttonPopularTag1 = view.findViewById(R.id.buttonPopularTag1);
        buttonPopularTag2 = view.findViewById(R.id.buttonPopularTag2);
        buttonPopularTag3 = view.findViewById(R.id.buttonPopularTag3);

        buttonsList.add(buttonTag1);
        buttonsList.add(buttonTag2);
        buttonsList.add(buttonTag3);
        buttonsList.add(buttonTag4);
        buttonsList.add(buttonTag5);
        buttonsList.add(buttonTag6);
        buttonsList.add(buttonTag7);
        buttonsList.add(buttonTag8);
        buttonsList.add(buttonTag9);
        buttonsList.add(buttonTag10);

        popularButtonsList.add(buttonPopularTag1);
        popularButtonsList.add(buttonPopularTag2);
        popularButtonsList.add(buttonPopularTag3);

        if (getTagsName == null) {
            getTagsName = new getTagsName();
        }

        setNewTagAdapter();

        //Instantiate asynctask
        if (getPopularTags == null) {
            getPopularTags = new getPopularTags();


            //Get popularTags on buttons
            try {
                String[] popularTagsListServer = getPopularTags.execute().get().split(":");

                buttonPopularTag1.setText(popularTagsListServer[2] + "    +");
                buttonPopularTag2.setText(popularTagsListServer[3] + "    +");
                buttonPopularTag3.setText(popularTagsListServer[4] + "    +");

                for (int i = 0; i < popularButtonsList.size(); i++) {
                    popularButtonsList.get(i).setVisibility(View.VISIBLE);
                }

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < 10; i++) {
            tagsList.add("-");
        }

        addTag.setOnClickListener(v -> {
            for (int i = 0; i < tagsList.size(); i++) {
                if (buttonsList.get(i).getText().toString().equals("-")) {
                    buttonsList.get(i).setVisibility(View.GONE);
                }

                if (!newTag.getText().toString().equals("") && tagsList.get(i).equals("-")) {
                    if (!tagsList.contains(newTag.getText().toString().toLowerCase().trim())) {
                        try {
                            getRelationTags = new getRelationTags();
                            String[] relationTags = getRelationTags.execute(newTag.getText().toString().toLowerCase().trim()).get().split(":");
                            if (relationTags.length > 2) {
                                ArrayList<String> listRelationTags = new ArrayList<>();
                                for (int j = 2; j < relationTags.length; j++) {
                                    listRelationTags.add(relationTags[j]);
                                }

                                for (int j = 0; j < popularButtonsList.size(); j++) {
                                    if (j <= listRelationTags.size() - 1) {
                                        popularButtonsList.get(j).setVisibility(View.VISIBLE);
                                        popularButtonsList.get(j).setText(listRelationTags.get(j) + "    +");
                                    } else {
                                        popularButtonsList.get(j).setVisibility(View.GONE);
                                    }
                                }
                            }
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }

                        errorText.setText("");
                        tagsList.set(i, newTag.getText().toString().toLowerCase().trim());
                        buttonsList.get(i).setVisibility(View.VISIBLE);
                        buttonsList.get(i).setText(tagsList.get(i) + "  X");
                        newTag.setText("");
                        break;
                    } else {
                        errorText.setText(R.string.repeated_tags);
                    }
                }
            }

            int contador = 0;
            for (int i = 0; i < tagsList.size(); i++) {
                if (!tagsList.get(i).equals("-")) {
                    contador++;
                }
            }

            if (contador == tagsList.size()) {
                errorText.setText(R.string.error_too_many_tags);
            } else {
                errorText.setText("");
            }
        });

        for (int i = 0; i < buttonsList.size(); i++) {
            Button removeTag = buttonsList.get(i);
            int num = i;

            removeTag.setOnClickListener(v -> {
                removeTag.setVisibility(View.GONE);
                tagsList.set(num, "-");

                int contador = 0;
                for (int j = 0; j < tagsList.size(); j++) {
                    if (!tagsList.get(j).equals("-")) {
                        contador++;
                    }
                }

                if (contador == tagsList.size()) {
                    errorText.setText(R.string.error_too_many_tags);
                } else {
                    errorText.setText("");
                }
            });
        }

        for (int i = 0; i < popularButtonsList.size(); i++) {
            Button addPopularTag = popularButtonsList.get(i);

            addPopularTag.setOnClickListener(v -> {
                for (int j = 0; j < tagsList.size(); j++) {
                    if (tagsList.get(j).equals("-")) {
                        String popularTagName = addPopularTag.getText().toString().toLowerCase().trim();

                        if (!tagsList.contains(popularTagName.substring(0, popularTagName.length() - 1).trim())) {
                            tagsList.set(j, popularTagName.substring(0, popularTagName.length() - 1).trim());
                            buttonsList.get(j).setVisibility(View.VISIBLE);
                            buttonsList.get(j).setText(tagsList.get(j) + "  X");

                            try {
                                getRelationTags = new getRelationTags();
                                String[] relationTags = getRelationTags.execute(tagsList.get(j).toLowerCase().trim()).get().split(":");
                                if (relationTags.length > 2) {
                                    ArrayList<String> listRelationTags = new ArrayList<>();
                                    for (int k = 2; k < relationTags.length; k++) {
                                        listRelationTags.add(relationTags[k]);
                                    }

                                    for (int k = 0; k < popularButtonsList.size(); k++) {
                                        if (k <= listRelationTags.size() - 1) {
                                            popularButtonsList.get(k).setVisibility(View.VISIBLE);
                                            popularButtonsList.get(k).setText(listRelationTags.get(k) + "    +");
                                        } else {
                                            popularButtonsList.get(k).setVisibility(View.GONE);
                                        }
                                    }
                                }
                            } catch (ExecutionException | InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    }
                }

            });
        }
        return view;
    }

    public static List<String> getTagsList() {
        return tagsList;
    }

    public static void restartTagsList() {
        tagsList.clear();
        for (int i = 0; i < 10; i++) {
            tagsList.add("-");
        }
    }

    class getPopularTags extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            out = MainActivity.out;
            in = MainActivity.in;

            out.println("CL:" + "popularTags");
            String popularTags = "";
            try {
                popularTags = in.readLine();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return popularTags;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }

    class getTagsName extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            out = MainActivity.out;
            in = MainActivity.in;

            out.println("CL:" + "tagsName");
            String tagsName = "";
            try {
                tagsName = in.readLine();

            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(tagsName);
            return tagsName;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }

    private void setNewTagAdapter() {
        try {
            String[] tagsNameFromServer = getTagsName.execute().get().split(":");
            List<String> tagsName = new ArrayList<>();

            for (int i = 2; i < tagsNameFromServer.length; i++) {
                System.out.println(tagsNameFromServer[i]);
                tagsName.add(tagsNameFromServer[i]);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, tagsName);
            newTag.setAdapter(adapter);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    class getRelationTags extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            out = MainActivity.out;
            in = MainActivity.in;

            out.println("CL:" + "relationTags:" + params[0]);
            String relationTags = "";
            try {
                relationTags = in.readLine();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return relationTags;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }
}