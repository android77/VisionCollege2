package com.example.seung.visioncollege;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class PostActivity extends AppCompatActivity{

    // 파이어베이스 DB로 데이터 Post 하는 법 강의 참고
    //https://www.youtube.com/watch?v=x0ScnHJi8WY&list=PLGCjwl1RrtcTXrWuRTa59RyRmQ4OedWrt&index=19
    private ImageButton mSelectImage;
    private Button btnPost;
    private EditText mPostTitle, mPostDesc;
    private DatabaseReference mDatabase;
    private Uri mImageUri = null;
    private StorageReference mStorage;
    private ProgressDialog progressDialog;
    private static final int GALLERRY_REQUEST = 1;
    // 게시물 정렬을 위한 카운트 변수 선언

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        Intent intent = getIntent();
        final String username = intent.getStringExtra("username");

        // 데이터 쓰기 진행중이라고  유저에게 알려주는 다이얼로그.
        progressDialog = new ProgressDialog(this);

        mSelectImage = findViewById(R.id.imageButton);
        btnPost = findViewById(R.id.btn_post);
        mPostTitle = findViewById(R.id.titleField);
        mPostDesc = findViewById(R.id.descField);


        // 파베 디비에서 데이터베이스 참조하기
        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("post");
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // 이미지 버튼 클릭 이벤트 발생

        mSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERRY_REQUEST);

            }
        });

        // 포스트 버튼 클릭시, 디비에 게시글 정보 저장하는 이벤트 발생

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                postMethod();
            }
        });
    }

    // Post 메소드

    private void postMethod(){

        progressDialog.setMessage("올리는 중...");
        progressDialog.show();

        final String title_val = mPostTitle.getText().toString().trim();
        final String desc_val = mPostDesc.getText().toString().trim();

//        final Timestamp timestamp = new Timestamp(Calendar.getInstance().getTimeInMillis());

        if (!TextUtils.isEmpty(title_val) && !TextUtils.isEmpty(desc_val) && mImageUri != null){

            StorageReference filepath = mStorage.child("Post_Images").child(mImageUri.getLastPathSegment());
            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    DatabaseReference newPost = mDatabase.push();
                    newPost.child("title").setValue(title_val);
                    newPost.child("desc").setValue(desc_val);
                    newPost.child("image").setValue(downloadUrl.toString());
                    // 게시물 시간 체크
                    newPost.child("date").setValue(currentTime());

                    // -1 을 곱해서 orderByChild 로 데이터 정렬할때, 오름차순 기본 형식일때, DB 데이터 거꾸로 정렬해서 뿌리게 하기 위한 데이터.
                    newPost.child("count").setValue(-1 * new Date().getTime());

                    progressDialog.dismiss();
                    startActivity(new Intent(PostActivity.this, LoggedActivity.class));
                    finish();
                }
            });
        }
    }

    // Date Function
    private String currentTime(){

        long time = System.currentTimeMillis();
        SimpleDateFormat dayTime = new SimpleDateFormat("yyyy년 MM월 dd일 hh시 mm분 ss초 a");
        dayTime.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        String str = dayTime.format(new Date(time));
        return str;
    };

    // 이미지버튼 클릭시,
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERRY_REQUEST && resultCode == RESULT_OK){

            mImageUri = data.getData();

            mSelectImage.setImageURI(mImageUri);

        }
    }
}