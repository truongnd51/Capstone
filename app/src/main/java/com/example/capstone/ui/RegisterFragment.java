package com.example.capstone.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.capstone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {

    private EditText edtEmail, edtPassword, edtPhone, edtName, edtConfirmPassword;
    private Button btnRegister;
    private FirebaseFirestore db;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RegisterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        db = FirebaseFirestore.getInstance();

        edtEmail = view.findViewById(R.id.edtEmail);
        edtPassword = view.findViewById(R.id.edtPassword);
        edtConfirmPassword = view.findViewById(R.id.edtConfirmPassword);
        edtPhone = view.findViewById(R.id.edtPhone);
        edtName = view.findViewById(R.id.edtName);
        btnRegister = view.findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> registerUser());

        return view;
    }

    private void registerUser() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();
        String name = edtName.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || name.isEmpty() || phone.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng kiểm tra lại thông tin đăng ký", Toast.LENGTH_SHORT).show();
            return;
        } else if (!password.equals(confirmPassword)) {
            Toast.makeText(getContext(), "Mật khẩu không trùng khớp", Toast.LENGTH_SHORT).show();
            return;
        } else if (!isValidEmail(email)){
            Toast.makeText(getContext(), "Vui lòng nhập đúng định dạng email", Toast.LENGTH_SHORT).show();
            return;
        } else if (!isValidPhoneNumber(phone)){
            Toast.makeText(getContext(), "Vui lòng nhập đúng định dạng số điện thoại - 10 số", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Kiểm tra xem tên và email đã tồn tại trong Firestore hay chưa
        db.collection("User")
                .whereEqualTo("Email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        Toast.makeText(getContext(), "Email đã được đăng ký", Toast.LENGTH_SHORT).show();
                    } else {
                        db.collection("User")
                                .whereEqualTo("Name", name)
                                .get()
                                .addOnCompleteListener(task2 -> {
                                    if (task2.isSuccessful() && !task2.getResult().isEmpty()) {
                                        Toast.makeText(getContext(), "Tên người dùng đã được đăng ký", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // Lấy giá trị id cao nhất hiện tại
                                        db.collection("User")
                                                .orderBy("id", Query.Direction.DESCENDING)
                                                .limit(1)
                                                .get()
                                                .addOnCompleteListener(task3 -> {
                                                    int newID = 1; // Mặc định nếu không có người dùng nào

                                                    if (task3.isSuccessful() && !task3.getResult().isEmpty()) {
                                                        DocumentSnapshot lastUser = task3.getResult().getDocuments().get(0);
                                                        newID = lastUser.getLong("id").intValue() + 1;
                                                    }

                                                    // Thêm người dùng mới với id tăng dần
                                                    Map<String, Object> userData = new HashMap<>();
                                                    userData.put("id", newID);
                                                    userData.put("Email", email);
                                                    userData.put("Password", password); // Nên mã hóa trước khi lưu trữ
                                                    userData.put("Phone", phone);
                                                    userData.put("Name", name);
                                                    userData.put("LibraryID", 0);
                                                    userData.put("created_at", FieldValue.serverTimestamp());

                                                    db.collection("User").document()
                                                            .set(userData)
                                                            .addOnSuccessListener(aVoid -> {
                                                                Toast.makeText(getContext(), "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                                                                // Chuyển sang fragment đăng nhập
                                                                openLoginFragment();
                                                            })
                                                            .addOnFailureListener(e -> {
                                                                Toast.makeText(getContext(), "Đăng ký thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                            });
                                                });
                                    }
                                });
                    }
                });
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null)
            return false;
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public final static boolean isValidPhoneNumber(CharSequence target) {
        if (target == null) {
            return false;
        }
        // Kiểm tra định dạng số điện thoại (10-11 chữ số và bắt đầu bằng 0)
        String phonePattern = "^(0[0-9]{9,10})$";
        return target.toString().matches(phonePattern);
    }


    private void openLoginFragment() {
        // Chuyển sang LoginFragment
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.loginFragment);
    }
}