package com.sneva.smoji.sample;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.widget.Switch;

import com.sneva.smoji.view.SmojiUtils;

import com.sneva.smoji.view.preset.PresetEmojiLoader;
import com.sneva.smoji.view.view.EmojiButton;
import com.aghajari.sample.emojiview.activity.DarkActivity;
import com.aghajari.sample.emojiview.activity.EmojiActivity;
import com.aghajari.sample.emojiview.activity.EmojiPopupViewActivity;

public class MainActivity extends AppCompatActivity {

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //AXEmojiManager.filterEmojis(Arrays.asList("😀", "😃", "😄", "😁"));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Switch mEmojiView = findViewById(R.id.emoji_view_switch);
        final Switch mSingleEmojiView = findViewById(R.id.single_emoji_view_switch);
        final Switch mStickerView = findViewById(R.id.sticker_view_switch);
        final Switch mCustomView = findViewById(R.id.custom_view_switch);
        final Switch mFooterView = findViewById(R.id.footer_switch);
        final Switch mCustomFooterView = findViewById(R.id.custom_footer_switch);
        final Switch mWhiteCategory = findViewById(R.id.theme_switch);
        final Switch mPopupView = findViewById(R.id.popup_view);
        final Switch mDarkTheme = findViewById(R.id.dark_theme);

        final EmojiButton btn = findViewById(R.id.start_emoji_activity);

        PresetEmojiLoader.preloadEmoji(SmojiUtils.getEmojiUnicode(0x1f60d),
                emoji -> {
                    getSupportActionBar().setTitle(SmojiUtils.replaceWithEmojis(MainActivity.this,
                            "AXEmojiView " + emoji, 20));

                    btn.setText("Start Emoji Activity " + emoji);
                });

        btn.setOnClickListener(view -> {
            UI.mEmojiView = mEmojiView.isChecked();
            UI.mSingleEmojiView = mSingleEmojiView.isChecked();
            UI.mStickerView = mStickerView.isChecked();
            UI.mCustomView = mCustomView.isChecked();
            UI.mFooterView = mFooterView.isChecked();
            UI.mCustomFooter = mCustomFooterView.isChecked();
            UI.mWhiteCategory = mWhiteCategory.isChecked();

            if (mDarkTheme.isChecked()) {
                UI.loadDarkTheme();

                Intent intent = new Intent(MainActivity.this, DarkActivity.class);
                MainActivity.this.startActivity(intent);
            } else {
                UI.loadTheme();

                Intent intent = new Intent(MainActivity.this,
                        (mPopupView.isChecked() ? EmojiPopupViewActivity.class : EmojiActivity.class));
                intent.putExtra("load", true);
                MainActivity.this.startActivity(intent);
            }
        });
    }

}
