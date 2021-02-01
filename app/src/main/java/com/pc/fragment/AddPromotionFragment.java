package com.pc.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pc.R;
import com.pc.adapter.ChooseListAdapter;
import com.pc.model.Poster;
import com.pc.model.ShoppingList;
import com.pc.util.NavigationAddPromotion;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddPromotionFragment extends Fragment {

    @BindView(R.id.price)
    EditText priceEditText;
    @BindView(R.id.calendar)
    CalendarView calendarView;
    @BindView(R.id.add_btn)
    Button addButton;

    private NavigationAddPromotion navigation;
    private int posterId;
    private Date selectedDate = new Date();
    private Calendar calendar;
    private Double oldPrice;

    public AddPromotionFragment(int posterId, Double oldPrice) {
        this.posterId = posterId;
        this.oldPrice = oldPrice;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof NavigationAddPromotion) {
            navigation = (NavigationAddPromotion) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_promotion, container, false);
        ButterKnife.bind(this, view);
        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            calendar = new GregorianCalendar(year, month, dayOfMonth);
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @OnClick(R.id.add_btn)
    public void onAddButtonClick() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Date now = new Date();
        if(calendar==null){
            Toast.makeText(getContext(), "Nie wybrałeś daty", Toast.LENGTH_SHORT).show();
        }else {
            selectedDate = calendar.getTime();
            if (selectedDate.before(now)) {
                Toast.makeText(getContext(), "Nie możesz wybrać przeszłej daty", Toast.LENGTH_SHORT).show();
            } else {
                if (validatePrice()) {
                    Poster poster = new Poster(Double.parseDouble(priceEditText.getText().toString()), dateFormat.format(selectedDate));
                    navigation.addPromotion(poster);
                }
            }
        }
    }

    @OnClick(R.id.exit_btn)
    public void onExitButtonClick() {
        navigation.exit();
    }

    private boolean validatePrice() {
        if (priceEditText.getText().toString().equals("")) {
            Toast.makeText(getContext(), "Niepoprawna cena", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (oldPrice < Double.parseDouble(priceEditText.getText().toString())) {
            Toast.makeText(getContext(), "Cena promocyjna nie może być wyższa", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}
