package com.gmail.vanyadubik.managerplus.fragment;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.gmail.vanyadubik.managerplus.R;
import com.gmail.vanyadubik.managerplus.activity.VisitDetailActivity;
import com.gmail.vanyadubik.managerplus.adapter.VisitPlaneListAdapter;
import com.gmail.vanyadubik.managerplus.adapter.tabadapter.FragmentBecameVisibleInterface;
import com.gmail.vanyadubik.managerplus.app.ManagerPlusAplication;
import com.gmail.vanyadubik.managerplus.db.MobileManagerContract;
import com.gmail.vanyadubik.managerplus.model.db.document.Visit_Document;
import com.gmail.vanyadubik.managerplus.model.db.element.Client_Element;
import com.gmail.vanyadubik.managerplus.model.documents.VisitList;
import com.gmail.vanyadubik.managerplus.repository.DataRepository;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import org.joda.time.LocalDateTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;


public class VisitPlanFragment extends Fragment implements FragmentBecameVisibleInterface {
    private static  final int LAYOUT = R.layout.fragment_visit_plane;
    @Inject
    DataRepository dataRepository;

    private View view;
    private MaterialCalendarView materialCalendarView;
    private ListView listView;
    private List<VisitList> list;
    private TextView selectedDateTV;
    private Calendar selectedDate;

    public static VisitPlanFragment getInstance() {

        Bundle args = new Bundle();
        VisitPlanFragment fragment = new VisitPlanFragment();
        fragment.setArguments(args);
        return  fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);
        ((ManagerPlusAplication) getActivity().getApplication()).getComponent().inject(this);

        materialCalendarView = (MaterialCalendarView) view.findViewById(R.id.calendarView);
        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                selectedDate = date.getCalendar();
                initData(selectedDate);
            }
        });

        materialCalendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                selectedDate = date.getCalendar();
                initDatainCalendarView(date.getCalendar());
            }
        });

        selectedDateTV = (TextView) view.findViewById(R.id.selected_date);
        listView = (ListView) view.findViewById(R.id.visitplane_list_view);

        FloatingActionButton visitAddBtn = (FloatingActionButton) view.findViewById(R.id.visitplane_add_bt);
        visitAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), VisitDetailActivity.class);
                selectedDate.set(Calendar.HOUR_OF_DAY, 0);
                selectedDate.set(Calendar.MINUTE, 0);
                selectedDate.set(Calendar.SECOND, 0);
                intent.putExtra(MobileManagerContract.VisitContract.DATE, selectedDate.getTime().getTime());
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        selectedDate = Calendar.getInstance();

        materialCalendarView.addDecorator(
                new DayDecorator(CalendarDay.from(selectedDate.getTime()), DayDecorator.TYPE_DAY_TODAY));

        materialCalendarView.setSelectedDate(selectedDate);

        materialCalendarView.setCurrentDate(selectedDate);

        initData(selectedDate);

        initDatainCalendarView(selectedDate);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBecameVisible() {

    }

    @Override
    public void onBecameUnVisible() {

    }

    private void initData(Calendar calendar){

        Calendar cal = calendar;
        selectedDateTV.setText(getActivity().getResources().getString(R.string.visit_name_to)
                         + " " + new SimpleDateFormat("dd MMMM yyyy").format(calendar.getTime()));

        list = new ArrayList<>();

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Date dateStart = cal.getTime();

        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        Date dateEnd = cal.getTime();

        List<Visit_Document> visits = dataRepository.getVisitByPeriod(dateStart, dateEnd);

        for (Visit_Document visit : visits) {
            VisitList visitList = new VisitList(visit.getExternalId(), visit.getDate(), visit.getTypeVisit());
            Client_Element client = dataRepository.getClient(visit.getClientExternalId());
            if (client != null) {
                visitList.setClient(client.getName());
            }
            list.add(visitList);
        }

        VisitPlaneListAdapter adapter = new VisitPlaneListAdapter(getActivity(), list);
        listView.setAdapter(adapter);
    }

    private void initDatainCalendarView(Calendar calendar){

        Calendar cal = calendar;

        cal.set(Calendar.DATE, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Date dateStart = cal.getTime();

        cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        Date dateEnd = cal.getTime();

        List<Visit_Document> visits = dataRepository.getVisitByPeriod(dateStart, dateEnd);

        ArrayList listDecor = new ArrayList();
        for (Visit_Document visit : visits) {
            CalendarDay day = CalendarDay.from(visit.getDate());
            listDecor.add((new DayDecorator(day,
                    visit.getDate().getTime() > LocalDateTime.now().toDate().getTime()
                            ? DayDecorator.TYPE_DAY_PLANE : DayDecorator.TYPE_DAY_PAST)));
        }
        materialCalendarView.addDecorators(listDecor);

    }

    private class DayDecorator implements DayViewDecorator {
        private static final int TYPE_DAY_PAST = 0;
        private static final int TYPE_DAY_TODAY = 1;
        private static final int TYPE_DAY_PLANE = 2;
        private CalendarDay calendarDay;
        private Drawable drawable;

        public DayDecorator(CalendarDay date,int typeDay) {
            calendarDay = date;
            switch (typeDay) {
                case TYPE_DAY_PAST:
                    drawable = getActivity().getResources().getDrawable(R.drawable.decorator_day_past);
                    break;
                case TYPE_DAY_TODAY:
                    drawable = getActivity().getResources().getDrawable(R.drawable.decorator_today);
                    break;
                case TYPE_DAY_PLANE:
                    drawable = getActivity().getResources().getDrawable(R.drawable.decorator_dayplane);
                    break;
            }
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return calendarDay != null && day.equals(calendarDay);
        }


        @Override
        public void decorate(DayViewFacade view) {
            view.setBackgroundDrawable(drawable);
        }

    }
}
