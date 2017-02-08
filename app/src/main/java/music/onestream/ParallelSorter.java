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

    String[] Array1;
    Uri[] Array2;
    String[] Array3;
    Integer[] Array4;
    String type;
    Object[] retArr = null;

    public ParallelSorter(String[] Array1, Uri[] Array2, String[] Array3, Integer[] Array4,  String type)
    {
        //Note: only 2 of these arrays should be non-null. Either 2,3, 3,4 or 2,4 are null
        this.Array1 = Array1;
        this.Array2 = Array2;
        this.Array3 = Array3;
        this.Array4 = Array4;
        this.type = type;

        if (this.Array2 != null) {
            sortUri();
            retArr = new Object[]{Array1 , Array2};
        }
        else if (this.Array3 != null)
        {
            sortString();
            retArr = new Object[]{Array1 , Array3};
        }
        else if (this.Array4 != null)
        {
            sortInt();
            retArr = new Object[]{Array1 , Array4};
        }
        else
        {
            return;
        }
    }

        public Object[] getRetArr() {
            return retArr;
        }

        public void sortUri() {

            ArrayList<compUri> metaData= new ArrayList<compUri>();
            for (int i = 0; i < Array1.length; i++)
            {
                metaData.add(new compUri(Array1[i],Array2[i]));
            }

                Collections.sort(metaData, new ResultComparatorURI(type));
                for(int i =0; i < metaData.size(); i++)
                {
                    compUri comp = metaData.get(i);
                    Array1[i] = comp.output;
                    Array2[i] = comp.result;
                }
        }

        public void sortString() {

            ArrayList<compString> metaData= new ArrayList<compString>();
            for (int i = 0; i < Array1.length; i++)
            {
                metaData.add(new compString(Array1[i],Array3[i]));
            }

            Collections.sort(metaData, new ResultComparatorString(type));
            for(int i =0; i < metaData.size(); i++) {
                compString comp = metaData.get(i);
                Array1[i] = comp.output;
                Array3[i] = comp.result;
            }
        }

        public void sortInt() {

            ArrayList<compInt> metaData= new ArrayList<compInt>();
            for (int i = 0; i < Array1.length; i++)
            {
                metaData.add(new compInt(Array1[i], Array4[i]));
            }

            Collections.sort(metaData, new ResultComparatorInt(type));
            for(int i =0; i < metaData.size(); i++) {
                compInt comp = metaData.get(i);
                Array1[i] = comp.output;
                Array4[i] = comp.result;
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

    class ResultComparatorInt implements Comparator<compInt> {
        String type;

        public ResultComparatorInt(String type) {
            this.type = type;
        }
        @Override
        public int compare(compInt a, compInt b) {
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

    class compString{
        String output;
        String result;

        compString(String n, String a) {
            output = n;
            result = a;
        }
    }

    class compInt {
        String output;
        int result;

        compInt(String n, int a) {
            output = n;
            result = a;
        }
    }
