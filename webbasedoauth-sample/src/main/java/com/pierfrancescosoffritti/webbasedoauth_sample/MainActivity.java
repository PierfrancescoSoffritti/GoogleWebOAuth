package com.pierfrancescosoffritti.webbasedoauth_sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.http.HttpHeaders;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.pierfrancescosoffritti.webbasedoauth.Authenticator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final String CLIENT_ID = "345651501550-fqjptstl9tok5hf2ek0ti0feu8h3ghv2.apps.googleusercontent.com";
    private static final String REDIRECT_URI="http://localhost";
    private static final String TOKEN_URL ="https://accounts.google.com/o/oauth2/token";
    private static final String OAUTH_URL ="https://accounts.google.com/o/oauth2/auth";

    private static final String RESPONSE_TYPE = "code";
    private static final String ACCESS_TYPE = "offline";
    private static final String CLIENT_SECRET = null;

    private static final String[] scopes = new String[] {YouTubeScopes.YOUTUBE, YouTubeScopes.YOUTUBE_UPLOAD};

    private Authenticator authenticator;

    @BindView(R.id.channel_title_text_view) TextView channelTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        authenticator = new Authenticator(this, OAUTH_URL, scopes, REDIRECT_URI, RESPONSE_TYPE, CLIENT_ID, ACCESS_TYPE, TOKEN_URL, CLIENT_SECRET);
    }

    @OnClick(R.id.fetch_channel_title_button)
    public void askForChannelTitle() {
        askForChannelTitle(authenticator);
    }

    @OnClick(R.id.logout_button)
    public void logout() {
        authenticator.logout();
        channelTitle.setText(" ");
    }

    private void askForChannelTitle(Authenticator authenticator) {
        new Thread() {
            public void run() {

                try {

                    String accessToken = authenticator.getAccessToken();
                    if(accessToken == null) {
                        channelTitle.post(() -> channelTitle.setText(""));
                        return;
                    }

                    HttpHeaders headers = new HttpHeaders();
                    headers.setAuthorization("Bearer " + accessToken);

                    YouTube youtube = new YouTube.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), request -> request.setHeaders(headers))
                            .setApplicationName("AppName")
                            .build();

                    YouTube.Channels.List request = youtube.channels().list("snippet");
                    request.setFields("items/snippet/title");

                    request.setMine(true);
                    request.setKey("AIzaSyA4hdKzU9AkD1o0ftGv3SvPfN4M99ur3Qg");

                    ChannelListResponse channels = request.execute();
                    Channel channel = channels.getItems().get(0);

                    String title = channel.getSnippet().getTitle();
                    Log.d(MainActivity.class.getSimpleName(), "Channel title: " +title);
                    channelTitle.post(() -> channelTitle.setText(title));

                } catch (Exception e) {
                    authenticator.handleException(e);
                }
            }
        }.start();
    }
}
