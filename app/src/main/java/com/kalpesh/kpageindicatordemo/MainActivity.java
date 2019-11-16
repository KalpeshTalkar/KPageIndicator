package com.kalpesh.kpageindicatordemo;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.kalpesh.kpageindicatorlibrary.KPageIndicator;


public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {

    KPageIndicator pageIndicator;
    EditText pageCountFld;
    RadioGroup animGrp, styleGrp;

    int currentPage = 0;
    int pageCount = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pageIndicator = findViewById(R.id.pageIndicator);
        pageCountFld = findViewById(R.id.pageCountFld);
        animGrp = findViewById(R.id.animationGrp);
        styleGrp = findViewById(R.id.styleGrp);
        animGrp.setOnCheckedChangeListener(this);
        styleGrp.setOnCheckedChangeListener(this);
    }

    public void setPageCount(View v) {
        String text = pageCountFld.getText().toString();
        pageCount = 2;
        if (!text.isEmpty()) {
            try {
                pageCount = Integer.parseInt(text);
                if (pageCount < 2) {
                    pageCount = 2;
                }
            } catch (Exception ignored) {
            }
        }
        currentPage = 0;
        pageIndicator.setCurrentPage(0);
        pageIndicator.setPageCount(pageCount);
    }

    public void next(View v) {
        currentPage++;
        if (currentPage >= pageCount) {
            currentPage = pageCount - 1;
        }
        pageIndicator.setCurrentPage(currentPage);
    }

    public void previous(View v) {
        currentPage--;
        if (currentPage < 0) {
            currentPage = 0;
        }
        pageIndicator.setCurrentPage(currentPage);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.animNone:
                pageIndicator.setIndicatorAnimation(KPageIndicator.ANIMATION_NONE);
                break;

            case R.id.animLinear:
                pageIndicator.setIndicatorAnimation(KPageIndicator.ANIMATION_LINEAR);
                break;

            case R.id.animEnlarge:
                pageIndicator.setIndicatorAnimation(KPageIndicator.ANIMATION_ENLARGE);
                break;

            case R.id.styleDefault:
                pageIndicator.setIndicatorStyle(KPageIndicator.STYLE_DEFAULT);
                break;

            case R.id.styleEnlarged:
                pageIndicator.setIndicatorStyle(KPageIndicator.STYLE_ENLARGED);
                break;

            default:
                break;
        }
    }
}
