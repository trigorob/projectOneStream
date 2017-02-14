package music.onestream;

/**
 * Created by ruspe_000 on 2017-02-07.
 */

import android.icu.util.RangeValueIterator;
import android.net.Uri;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ParallelSorter {

    ArrayList<String[]> Array1;
    Uri[] Array2;
    ArrayList<String> Array3;
    Integer[] Array4;
    String type;
    String[][] Array5;
    Object[] retArr = null;

    public ParallelSorter(ArrayList<String[]> Array1, Uri[] Array2, ArrayList<String> Array3, Integer[] Array4, String[][] Array5, String type)
    {
        //Note: only 2 of these arrays should be non-null. Either 2,3, 3,4 or 2,4 are null
        this.Array1 = Array1;
        this.Array2 = Array2;
        this.Array3 = Array3;
        this.Array4 = Array4;
        this.Array5 = Array5;
        this.type = type;

        if (this.Array5 != null)
        {
            if (this.Array4 != null)
            {
                sortIntLocal();
                retArr = new Object[]{Array5 , Array4};
            }
            else if (this.Array2 != null) {
                sortUriLocal();
                retArr = new Object[]{Array5 , Array2};
            }
        }
        else if (this.Array3 != null)
        {
            sortString();
            retArr = new Object[]{Array1 , Array3};
        }
        else
        {
            return;
        }
    }

        public Object[] getRetArr() {
            return retArr;
        }

        public void sortUriLocal() {

            ArrayList<LocalCompUri> metaData= new ArrayList<LocalCompUri>();
            for (int i = 0; i < Array5.length; i++)
            {
                metaData.add(new LocalCompUri(Array5[i][0], Array5[i][1], Array2[i]));
            }

            Collections.sort(metaData, new LocalResultComparatorURI(type));
            for(int i =0; i < metaData.size(); i++)
            {
                LocalCompUri comp = metaData.get(i);
                Array5[i][0] = comp.output;
                Array5[i][1] = comp.artist;
                Array2[i]    = comp.result;
            }
        }

        public void sortIntLocal() {

            ArrayList<LocalCompInt> metaData= new ArrayList<LocalCompInt>();
            for (int i = 0; i < Array5.length; i++)
            {
                metaData.add(new LocalCompInt(Array5[i][0], Array5[i][1], Array4[i]));
            }

            Collections.sort(metaData, new LocalResultComparatorInt(type));
            for(int i =0; i < metaData.size(); i++)
            {
                LocalCompInt comp = metaData.get(i);
                Array5[i][0] = comp.output;
                Array5[i][1] = comp.artist;
                Array4[i]    = comp.result;
            }
        }

        public void sortString() {

            ArrayList<compString> metaData= new ArrayList<compString>();
            for (int i = 0; i < Array1.size(); i++)
            {
                metaData.add(new compString(Array1.get(i)[0], Array1.get(i)[1], Array3.get(i)));
            }

            Collections.sort(metaData, new ResultComparatorString(type));
            for(int i =0; i < metaData.size(); i++) {
                compString comp = metaData.get(i);
                Array1.set(i, new String[]{comp.output, comp.uri});
                Array3.set(i, comp.result);
            }
        }

    }


    class LocalResultComparatorURI implements Comparator<LocalCompUri> {
        String type;

        public LocalResultComparatorURI(String type) {
            this.type = type;
        }

        @Override
        public int compare(LocalCompUri a, LocalCompUri b) {
            if (type.equals("ALPH-ASC")) {
                return a.output.compareTo(b.output) < 0 ? -1 : a.output == b.output ? 0 : 1;
            }
            else if (type.equals("ALPH-DESC")) {
                return b.output.compareTo(a.output) < 0 ? -1 : b.output == a.output ? 0 : 1;
            }
            else {
                return 0;
            }
        }
    }


    class ResultComparatorURI implements Comparator<compUri> {
        String type;

        public ResultComparatorURI(String type) {
            this.type = type;
        }

        @Override
        public int compare(compUri a, compUri b) {
            if (type.equals("ALPH-ASC")) {
                return a.output.compareTo(b.output) < 0 ? -1 : a.output == b.output ? 0 : 1;
            }
            else if (type.equals("ALPH-DESC")) {
                return b.output.compareTo(a.output) < 0 ? -1 : b.output == a.output ? 0 : 1;
            }
            else {
                return 0;
            }
        }
    }

    class ResultComparatorString implements Comparator<compString> {
        String type;

        public ResultComparatorString(String type) {
            this.type = type;
        }
        @Override
        public int compare(compString a, compString b) {
            if (type.equals("ALPH-ASC")) {
                return a.output.compareTo(b.output) < 0 ? -1 : a.output == b.output ? 0 : 1;
            }
            else if (type.equals("ALPH-DESC")) {
                return b.output.compareTo(a.output) < 0 ? -1 : b.output == a.output ? 0 : 1;
            }
            else {
                return 0;
            }
        }
    }

    class LocalResultComparatorInt implements Comparator<LocalCompInt> {
        String type;

        public LocalResultComparatorInt(String type) {
            this.type = type;
        }
        @Override
        public int compare(LocalCompInt a, LocalCompInt b) {
            if (type.equals("ALPH-ASC")) {
                return a.output.compareTo(b.output) < 0 ? -1 : a.output == b.output ? 0 : 1;
            }
            else if (type.equals("ALPH-DESC")) {
                return b.output.compareTo(a.output) < 0 ? -1 : b.output == a.output ? 0 : 1;
            }
            else {
                return 0;
            }
        }
    }

    class compUri{
        String output;
        Uri result;

        compUri(String n, Uri a) {
            output = n;
            result = a;
        }
    }

    class LocalCompUri{
        String output;
        Uri result;
        String artist;

        LocalCompUri(String n, String art, Uri a) {
            output = n;
            result = a;
            artist = art;
        }
    }

    class LocalCompInt{
        String output;
        int result;
        String artist;

        LocalCompInt(String n, String art, int a) {
            output = n;
            result = a;
            artist = art;
        }
    }

    class compString{
        String output;
        String result;
        String uri;

        compString(String n, String a, String URI) {
            output = n;
            result = a;
            uri = URI;
        }
    }
