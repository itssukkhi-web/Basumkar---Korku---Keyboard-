package com.basumkar.korkukeyboard;

import android.graphics.Color;
import android.graphics.Typeface;
import android.inputmethodservice.InputMethodService;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class KorkuInputMethodService extends InputMethodService {

    private LinearLayout root;
    private Typeface korku;

    // These are Unicode Devanagari code points whose glyphs are drawn
    // by the Basumkar Korku font as Korku-script shapes.
    private final String[][] rows = {
        {"अ","आ","इ","ई","उ","ऊ","ऋ","ए","ऐ","ओ","औ","अं","अः"},
        {"क","ख","ग","घ","ङ","च","छ","ज","झ","ञ"},
        {"ट","ठ","ड","ढ","ण","त","थ","द","ध","न"},
        {"प","फ","ब","भ","म","य","र","ल","व","श"},
        {"ष","स","ह","ड़","ढ़","ळ","क्ष","त्र","ज्ञ","श्र"}
    };

    // Special Korku glyphs in the font's Private Use Area.
    private final String[] pua = {
        "\uE100", "\uE101", "\uE102", "\uE103"
    };

    @Override
    public View onCreateInputView() {

        // IMPORTANT: this is the exact uploaded font filename.
        korku = Typeface.createFromAsset(
                getAssets(),
                "fonts/Basumkar_Korku_v2_Linear.ttf"
        );

        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(2, 2, 2, 2);
        root.setBackgroundColor(Color.WHITE);

        for (String[] row : rows) {
            addRow(row);
        }

        LinearLayout bottom = new LinearLayout(this);
        bottom.setOrientation(LinearLayout.HORIZONTAL);

        addButton(bottom, "←", v -> {
            if (getCurrentInputConnection() != null) {
                getCurrentInputConnection().deleteSurroundingText(1, 0);
            }
        });

        addButton(bottom, "␠", v -> commit(" "));

        addButton(bottom, "।", v -> commit("।"));

        addButton(bottom, "↵", v -> {
            if (getCurrentInputConnection() != null) {
                getCurrentInputConnection().sendKeyEvent(
                    new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER)
                );
            }
        });

        root.addView(
            bottom,
            new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1
            )
        );

        return root;
    }

    private void addRow(String[] chars) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);

        for (String c : chars) {
            addButton(row, c, v -> commit(mapped(c)));
        }

        root.addView(
            row,
            new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1
            )
        );
    }

    private void addButton(
            LinearLayout row,
            String text,
            View.OnClickListener listener) {

        Button b = new Button(this);
        b.setText(text);
        b.setTextSize(18);
        b.setTypeface(korku);
        b.setAllCaps(false);
        b.setTextColor(Color.BLACK);
        b.setOnClickListener(listener);

        row.addView(
            b,
            new LinearLayout.LayoutParams(0, -1, 1)
        );
    }

    private String mapped(String s) {
        if (s.equals("क्ष")) return pua[0];
        if (s.equals("त्र")) return pua[1];
        if (s.equals("ज्ञ")) return pua[2];
        if (s.equals("श्र")) return pua[3];
        return s;
    }

    private void commit(String s) {
        if (getCurrentInputConnection() != null) {
            getCurrentInputConnection().commitText(s, 1);
        }
    }
}
