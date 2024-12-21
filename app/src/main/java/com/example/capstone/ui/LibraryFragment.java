package com.example.capstone.ui;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.capstone.R;
import com.example.capstone.adapter.LibraryAdapter;
import com.example.capstone.model.Library;
import com.example.capstone.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.Source;
import com.google.protobuf.StringValue;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LibraryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LibraryFragment extends Fragment implements LibraryAdapter.OnLibraryItemClickListener, LibraryAdapter.OnShareLibraryListener, LibraryAdapter.OnUpdateLibraryListener {

    private RecyclerView recyclerView;
    private LibraryAdapter libraryAdapter;
    private List<Library> libraryList;
    private FirebaseFirestore db;
    private SearchView librarySearch;
    private boolean isSearchStarted = false;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LibraryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LibraryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LibraryFragment newInstance(String param1, String param2) {
        LibraryFragment fragment = new LibraryFragment();
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
        View view = inflater.inflate(R.layout.fragment_library, container, false);

        librarySearch = view.findViewById(R.id.search_view);

        recyclerView = view.findViewById(R.id.recyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        libraryList = new ArrayList<>();
        libraryAdapter = new LibraryAdapter(libraryList, this, this, this);
        recyclerView.setAdapter(libraryAdapter);

        db = FirebaseFirestore.getInstance();

        librarySearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchLibraryByName(query.trim());
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Kiểm tra nếu người dùng đã bắt đầu tìm kiếm
                if (!isSearchStarted && !newText.trim().isEmpty()) {
                    isSearchStarted = true; // Đánh dấu là người dùng đã bắt đầu tìm kiếm
                }

                // Chỉ thực hiện khi người dùng đã bắt đầu tìm kiếm và xóa văn bản
                if (newText.trim().isEmpty() && isSearchStarted) {
                    // Khi SearchView trống, tải lại danh sách
                    libraryList.clear();
                    loadLibraryData();
                    isSearchStarted = false; // Đặt lại flag sau khi đã xóa
                }
                return false;
            }
        });


        loadLibraryData();

        Button btAdd = view.findViewById(R.id.btn_add_library);
        btAdd.setOnClickListener(v -> showAddLibraryDialog());


        return view;
    }

    private void loadLibraryData() {
        // Tải các thư viện mặc định với UserId = 0
        db.collection("Library")
                .whereEqualTo("UserID", 0) // Các thư viện mặc định có UserId là 0
                .orderBy("id")
                .get(Source.SERVER)
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            libraryList.clear(); // Xóa danh sách hiện tại trước khi thêm dữ liệu mới
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                Library library = doc.toObject(Library.class);
                                libraryList.add(library); // Thêm các thư viện mặc định
                            }
                            // Load thư viện của người dùng sau khi đã tải thư viện mặc định
                            loadUserLibraryData();
                        } else {
                            Log.w("Firestore Error", "Error getting default libraries.", task.getException());
                            Toast.makeText(getContext(), "Lỗi khi lấy dữ liệu: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void loadUserLibraryData() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("id", 0);

        db.collection("Library")
                .whereEqualTo("UserID", userId) // Chỉ lấy các thư viện của người dùng hiện tại
                .orderBy("id")
                .get(Source.SERVER)
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                Library library = doc.toObject(Library.class);
                                libraryList.add(library); // Thêm thư viện của người dùng vào danh sách
                            }
                            libraryAdapter.notifyDataSetChanged(); // Cập nhật RecyclerView
                        } else {
                            Log.w("Firestore Error", "Error getting user libraries.", task.getException());
                            Toast.makeText(getContext(), "Lỗi khi lấy dữ liệu: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    @Override
    public void onLibraryItemSelected(Library library) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        int userID = sharedPreferences.getInt("id", 0);

        db.collection("User")
                .whereEqualTo("id", userID)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if(!queryDocumentSnapshots.isEmpty()){
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        db.collection("User")
                                .document(documentSnapshot.getId())
                                .update("LibraryID", library.getID())
                                .addOnSuccessListener(aVoid -> {
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putInt("LibraryID", library.getID()); // Lưu LibraryIDChoose
                                    editor.commit();

                                    NavController navController = Navigation.findNavController(getView());
                                    navController.navigate(R.id.action_libraryFragment_to_homeFragment);
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(requireContext(), "Cập nhật LibraryID thất bại", Toast.LENGTH_SHORT).show();
                                });
                    }
                })
        .addOnFailureListener(e -> {
            Log.e("Firestore", "Error getting documents", e);
            Toast.makeText(requireContext(), "Lỗi khi lấy dữ liệu người dùng", Toast.LENGTH_SHORT).show();
        });

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference timeLightOn = database.getReference("devices").child("TimeLightOn");
        timeLightOn.child("status").setValue(library.getLightOn());
        DatabaseReference timeLightOff = database.getReference("devices").child("TimeLightOff");
        timeLightOff.child("status").setValue(library.getLightOff());
        DatabaseReference humidityDown = database.getReference("devices").child("HumidityDown");
        humidityDown.child("status").setValue(library.getHumidity());
        DatabaseReference humidityUp = database.getReference("devices").child("HumidityUp");
        humidityUp.child("status").setValue(library.getHumidityUp());
        DatabaseReference SoilDown = database.getReference("devices").child("SoilDown");
        SoilDown.child("status").setValue(library.getSoilDown());
        DatabaseReference SoilUp = database.getReference("devices").child("SoilUp");
        SoilUp.child("status").setValue(library.getSoilUp());
        DatabaseReference TempDown = database.getReference("devices").child("TempDown");
        TempDown.child("status").setValue(library.getTemp());
        DatabaseReference TempUp = database.getReference("devices").child("TempUp");
        TempUp.child("status").setValue(library.getTempUp());

        DatabaseReference Delete = database.getReference("devices").child("delete");
        Delete.setValue("true");

        DatabaseReference mode = database.getReference("devices").child("Mode");
        mode.setValue("Auto");

        deleteAllLogs();

    }

    private void showAddLibraryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Thêm Thư Viện Mới");

        // Sử dụng layout tùy chỉnh cho dialog
        View dialogView = getLayoutInflater().inflate(R.layout.add_library, null);
        builder.setView(dialogView);

        // Lấy các trường nhập từ layout (bỏ qua trường ID)
        EditText edtName = dialogView.findViewById(R.id.edt_name);
        EditText edtCategoryID = dialogView.findViewById(R.id.edt_category_id);
        EditText edtHumidity = dialogView.findViewById(R.id.edt_humidity);
        EditText edtHumidityUp = dialogView.findViewById(R.id.edt_humidity_up);
        EditText edtTemp = dialogView.findViewById(R.id.edt_temp);
        EditText edtTempUp = dialogView.findViewById(R.id.edt_temp_up);
        EditText edtLightOff = dialogView.findViewById(R.id.edt_light_off);
        EditText edtLightOn = dialogView.findViewById(R.id.edt_light_on);
        EditText edtSoilDown = dialogView.findViewById(R.id.edt_soil_down);
        EditText edtSoilUp = dialogView.findViewById(R.id.edt_soil_up);

        edtCategoryID.setText("1");
        edtCategoryID.setEnabled(false);

        // Nút "Thêm" trong dialog
        Button btnConfirmAdd = dialogView.findViewById(R.id.btn_confirm_add);
        AlertDialog dialog = builder.create();

        btnConfirmAdd.setOnClickListener(v -> {
            // Kiểm tra dữ liệu đầu vào
            if (edtName.getText().toString().trim().isEmpty()) {
                edtName.setError("Tên thư viện không được để trống!");
                return;
            }

            try {
                float humidity = Float.parseFloat(edtHumidity.getText().toString());
                float humidityUp = Float.parseFloat(edtHumidityUp.getText().toString());
                float temp = Float.parseFloat(edtTemp.getText().toString());
                float tempUp = Float.parseFloat(edtTempUp.getText().toString());
                float lightOff = Float.parseFloat(edtLightOff.getText().toString());
                float lightOn = Float.parseFloat(edtLightOn.getText().toString());
                double soilDown = Double.parseDouble(edtSoilDown.getText().toString());
                double soilUp = Double.parseDouble(edtSoilUp.getText().toString());

                // Lấy ID cao nhất từ Firestore
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("Library")
                        .orderBy("id", Query.Direction.DESCENDING)
                        .limit(1)
                        .get()
                        .addOnCompleteListener(task -> {
                            int newID = 1; // Mặc định ID là 1 nếu không có thư viện nào

                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                DocumentSnapshot lastLibrary = task.getResult().getDocuments().get(0);
                                newID = lastLibrary.getLong("id").intValue() + 1; // Tăng ID lên 1
                            }

                            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
                            int userId = sharedPreferences.getInt("id", 0);

                            // Tạo map dữ liệu với các trường có chữ cái đầu viết hoa
                            Map<String, Object> libraryMap = new HashMap<>();
                            libraryMap.put("id", newID);
                            libraryMap.put("CategoryID", 1);
                            libraryMap.put("Name", edtName.getText().toString());
                            libraryMap.put("Humidity", humidity);
                            libraryMap.put("HumidityUp", humidityUp);
                            libraryMap.put("Temp", temp);
                            libraryMap.put("TempUp", tempUp);
                            libraryMap.put("LightOff", lightOff);
                            libraryMap.put("LightOn", lightOn);
                            libraryMap.put("SoilDown", soilDown);
                            libraryMap.put("SoilUp", soilUp);
                            libraryMap.put("UserID", userId);

                            // Thêm thư viện vào Firestore
                            db.collection("Library")
                                    .document()
                                    .set(libraryMap)
                                    .addOnSuccessListener(documentReference -> {
                                        // Hiển thị thông báo và đóng dialog
                                        Toast.makeText(getContext(), "Đã thêm thư viện thành công!", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                        loadLibraryData();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getContext(), "Lỗi khi thêm thư viện: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    });
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Lỗi khi lấy ID: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        });
            } catch (NumberFormatException e) {
                // Xử lý lỗi nếu nhập sai định dạng
                Toast.makeText(getContext(), "Vui lòng nhập đúng định dạng số và không được để trống!", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }


    private void deleteAllLogs() {
        db.collection("Log")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            // Duyệt qua tất cả các document và xóa từng cái
                            for (DocumentSnapshot document : task.getResult()) {
                                document.getReference().delete()
                                        .addOnSuccessListener(aVoid -> {
                                            // Đã xóa thành công
                                            Log.d("DeleteLogs", "Document deleted successfully.");
                                        })
                                        .addOnFailureListener(e -> {
                                            // Xử lý lỗi xóa
                                            Log.e("DeleteLogs", "Error deleting document: ", e);
                                        });
                            }

                            // Tạo một document mặc định trong collection để tránh collection bị xóa
                            db.collection("Log").document()
                                    .set(new HashMap<String, Object>())
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("DeleteLogs", "Default document created to preserve collection.");
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("DeleteLogs", "Error creating default document: ", e);
                                    });

                        } else {
                            // Xử lý lỗi nếu không thành công
                            if (task.getException() != null) {
                                Log.e("DeleteLogs", "Error getting documents: ", task.getException());
                            }
                        }
                    }
                });
    }


    @Override
    public void onShareLibrary(Library library) {
        // Hiển thị hộp thoại xác nhận
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Xác nhận chia sẻ");
        builder.setMessage("Bất kì ai cũng có thể thấy thư viện của bạn khi bạn chia sẻ. Bạn có chắc chắn không?");

        // Nút "Đồng ý"
        builder.setPositiveButton("Đồng ý", (dialog, which) -> {
            // Truy vấn tài liệu theo ID thư viện
            db.collection("Library")
                    .whereEqualTo("id", library.getID())  // Truy vấn tìm tài liệu có trường "ID" bằng với ID của thư viện
                    .get(Source.SERVER)
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // Nếu tìm thấy tài liệu, thực hiện cập nhật
                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                            db.collection("Library").document(documentSnapshot.getId())  // Dùng documentId từ kết quả tìm được
                                    .update("UserID", 0)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(getContext(), "Thư viện đã được chia sẻ!", Toast.LENGTH_SHORT).show();
                                        loadLibraryData(); // Reload danh sách thư viện
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getContext(), "Lỗi khi chia sẻ thư viện: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Toast.makeText(getContext(), "Thư viện không tồn tại.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Lỗi khi kiểm tra thư viện: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        // Nút "Hủy"
        builder.setNegativeButton("Hủy", (dialog, which) -> {
            dialog.dismiss(); // Đóng hộp thoại nếu người dùng chọn "Hủy"
        });

        // Hiển thị hộp thoại
        builder.create().show();
    }


    private void searchLibraryByName(String libraryName) {
        List<Library> searchResults = new ArrayList<>();

        // Lọc các thư viện trong libraryList để tìm kiếm theo tên
        for (Library library : libraryList) {
            if (library.getName().toLowerCase().contains(libraryName.toLowerCase())) {
                searchResults.add(library);
            }
        }

        // Kiểm tra nếu có kết quả tìm kiếm
        if (!searchResults.isEmpty()) {
            // Hiển thị kết quả tìm kiếm
            libraryAdapter.updateData(searchResults);
        }
    }

    @Override
    public void onUpdateLibrary(Library library) {
        showEditLibraryDialog(library);
    }

    private void showEditLibraryDialog(Library library) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Sửa Thư Viện");

        // Sử dụng layout tùy chỉnh
        View dialogView = getLayoutInflater().inflate(R.layout.edit_library, null);
        builder.setView(dialogView);

        // Điền dữ liệu hiện tại vào các trường
        EditText edtName = dialogView.findViewById(R.id.edt_name_edit);
        EditText edtHumidity = dialogView.findViewById(R.id.edt_humidity_d);
        EditText edtHumidityUp = dialogView.findViewById(R.id.edt_humidity_u);
        EditText edtTemp = dialogView.findViewById(R.id.edt_temp_d);
        EditText edtTempUp = dialogView.findViewById(R.id.edt_temp_u);
        EditText edtSoil = dialogView.findViewById(R.id.edt_soil_d);
        EditText edtSoilUp = dialogView.findViewById(R.id.edt_soil_u);
        EditText timeOff = dialogView.findViewById(R.id.time_off);
        EditText timeOn = dialogView.findViewById(R.id.time_on);

        edtName.setText(library.getName());
        edtHumidity.setText(String.valueOf(library.getHumidity()));
        edtHumidityUp.setText(String.valueOf(library.getHumidityUp()));
        edtTemp.setText(String.valueOf(library.getTemp()));
        edtTempUp.setText(String.valueOf(library.getTempUp()));
        edtSoil.setText(String.valueOf(library.getSoilDown()));
        edtSoilUp.setText(String.valueOf(library.getSoilUp()));
        timeOff.setText(String.valueOf(library.getLightOff()));
        timeOn.setText(String.valueOf(library.getLightOn()));

        Button btnSave = dialogView.findViewById(R.id.btn_save);
        AlertDialog dialog = builder.create();

        btnSave.setOnClickListener(v -> {
            if (edtName.getText().toString().trim().isEmpty()) {
                edtName.setError("Tên thư viện không được để trống!");
                return;
            }
            if (edtHumidity.getText().toString().trim().isEmpty()) {
                edtHumidity.setError("Độ ẩm không được để trống!");
                return;
            }
            if (edtHumidityUp.getText().toString().trim().isEmpty()) {
                edtHumidityUp.setError("Độ ẩm tối đa không được để trống!");
                return;
            }
            if (edtTemp.getText().toString().trim().isEmpty()) {
                edtTemp.setError("Nhiệt độ không được để trống!");
                return;
            }
            if (edtTempUp.getText().toString().trim().isEmpty()) {
                edtTempUp.setError("Nhiệt độ tối đa không được để trống!");
                return;
            }
            if (edtSoil.getText().toString().trim().isEmpty()) {
                edtSoil.setError("Độ ẩm đất không được để trống!");
                return;
            }
            if (edtSoilUp.getText().toString().trim().isEmpty()) {
                edtSoilUp.setError("Độ ẩm đất tối đa không được để trống!");
                return;
            }
            if (timeOff.getText().toString().trim().isEmpty()) {
                timeOff.setError("Thời gian tắt đèn không được để trống!");
                return;
            }
            if (timeOn.getText().toString().trim().isEmpty()) {
                timeOn.setError("Thời gian bật đèn sáng không được để trống!");
                return;
            }
            // Cập nhật thông tin thư viện
            Map<String, Object> updates = new HashMap<>();
            updates.put("Name", edtName.getText().toString());
            updates.put("Humidity", Float.parseFloat(edtHumidity.getText().toString()));
            updates.put("HumidityUp", Float.parseFloat(edtHumidityUp.getText().toString()));
            updates.put("Temp", Float.parseFloat(edtTemp.getText().toString()));
            updates.put("TempUp", Float.parseFloat(edtTempUp.getText().toString()));
            updates.put("SoilDown", Double.parseDouble(edtSoil.getText().toString()));
            updates.put("SoilUp", Double.parseDouble(edtSoilUp.getText().toString()));
            updates.put("LightOff", Float.parseFloat(timeOff.getText().toString()));
            updates.put("LightOn", Float.parseFloat(timeOn.getText().toString()));

            db.collection("Library")
                    .whereEqualTo("id", library.getID())
                    .get(Source.SERVER)
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                            db.collection("Library")
                                    .document(documentSnapshot.getId())
                                    .update(updates)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(requireContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                                            loadLibraryData();
                                            dialog.dismiss();
                                        } else {
                                            Toast.makeText(requireContext(), "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    });
        });

        dialog.show();
    }

}