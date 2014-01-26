package com.chanapps.glass.chan;

import android.app.Activity;
import android.os.Bundle;
import android.text.Layout;
import android.text.StaticLayout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.android.glass.app.Card;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;

import java.util.ArrayList;
import java.util.List;

public class ChanTextActivity extends Activity {

    public static final String TEXT = "text";

    private static final boolean DEBUG = true;
    private static final String TAG = ChanTextActivity.class.getSimpleName();

    private static final float SPACING_MULT = 1.0f;
    private static final float SPACING_ADD = 1.0f;
    private static final boolean INCLUDE_PAD = false;

    private String mText;
    private CardScrollView mCardScrollView;
    private TextCardScrollAdapter mAdapter;
    private List<Card> mCards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(TEXT))
            mText = savedInstanceState.getString(TEXT);
        else if (getIntent() != null && getIntent().hasExtra(TEXT))
            mText = getIntent().getStringExtra(TEXT);
        else {
            Log.e(TAG, "no text found");
            finish();
            return;
        }

        createCardsFromText();
        mAdapter = new TextCardScrollAdapter();

        View rootLayout = getLayoutInflater().inflate(R.layout.card_scroll_layout, null);
        mCardScrollView = (CardScrollView)rootLayout.findViewById(R.id.card_scroll_view);
        mCardScrollView.setAdapter(mAdapter);
        mCardScrollView.activate();
        setContentView(rootLayout);
    }

    private void createCardsFromText() {
        int textSize = getResources().getDimensionPixelSize(R.dimen.card_main_layout_main_min_textSize);
        int maxWidth = getResources().getDimensionPixelSize(R.dimen.card_main_layout_main_width);
        int maxHeight = getResources().getDimensionPixelSize(R.dimen.card_main_layout_main_height);

        TextView tv = new TextView(this);
        tv.setTextSize(textSize);
        tv.setText(mText);
        StaticLayout measure = new StaticLayout(tv.getText(), tv.getPaint(),
                maxWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, false);
        int numLines = measure.getLineCount();
        if (DEBUG) Log.i(TAG, "calculated lines=" + numLines + " for text=[" + mText + "]");

        int linesPerCard = maxHeight / textSize;
        int numFullCards = numLines / linesPerCard;
        int numCards = numFullCards + (numLines % linesPerCard > 0 ? 1 : 0);

        mCards = new ArrayList<Card>();
        for (int cardNum = 0; cardNum < numCards; cardNum++) {
            int startLine = cardNum * linesPerCard;
            int nextStartLine = startLine + linesPerCard;
            int startOffset = measure.getLineStart(startLine);
            int endOffset = (nextStartLine < numLines) ? measure.getLineStart(nextStartLine) : mText.length();
            String cardText = mText.substring(startOffset, endOffset);
            Card card = new Card(this);
            card.setText(cardText);
            String cardFootnote = getString(R.string.read_more_format, cardNum + 1, numCards);
            card.setFootnote(cardFootnote);
            mCards.add(card);
        }
    }

    private class TextCardScrollAdapter extends CardScrollAdapter {
        @Override
        public int findIdPosition(Object id) {
            return -1;
        }
        @Override
        public int findItemPosition(Object item) {
            return mCards.indexOf(item);
        }
        @Override
        public int getCount() {
            return mCards.size();
        }
        @Override
        public Object getItem(int position) {
            return mCards.get(position);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return mCards.get(position).toView();
        }
    }

}