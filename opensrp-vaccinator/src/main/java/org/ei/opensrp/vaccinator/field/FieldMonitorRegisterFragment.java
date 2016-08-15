package org.ei.opensrp.vaccinator.field;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.ei.opensrp.Context;
import org.ei.opensrp.adapter.SmartRegisterPaginatedAdapter;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.cursoradapter.CursorCommonObjectFilterOption;
import org.ei.opensrp.cursoradapter.CursorSortOption;
import org.ei.opensrp.cursoradapter.SmartRegisterCursorBuilder;
import org.ei.opensrp.cursoradapter.SmartRegisterPaginatedCursorAdapter;
import org.ei.opensrp.vaccinator.R;
import org.ei.opensrp.vaccinator.application.common.DateSort;
import org.ei.opensrp.vaccinator.application.common.ReportFilterOption;
import org.ei.opensrp.view.activity.SecuredNativeSmartRegisterActivity;
import org.ei.opensrp.view.controller.FormController;
import org.ei.opensrp.view.customControls.CustomFontTextView;
import org.ei.opensrp.view.dialog.DialogOption;
import org.ei.opensrp.view.dialog.FilterOption;
import org.ei.opensrp.view.dialog.SearchFilterOption;
import org.ei.opensrp.view.dialog.ServiceModeOption;
import org.ei.opensrp.view.dialog.SortOption;
import org.ei.opensrp.view.fragment.SecuredNativeSmartRegisterFragment;
import org.ei.opensrp.view.template.DetailActivity;
import org.ei.opensrp.view.template.SmartRegisterClientsProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import util.VaccinatorUtils;

import static android.view.View.TEXT_ALIGNMENT_CENTER;


/**
 * Created by Safwan on 2/15/2016.
 */
public class FieldMonitorRegisterFragment extends SecuredNativeSmartRegisterFragment {
    private final ClientActionHandler clientActionHandler = new ClientActionHandler();

    public FieldMonitorRegisterFragment(){
        super(null);
    }

    @SuppressLint("ValidFragment")
    public FieldMonitorRegisterFragment(FormController formController) {
        super(formController);
    }

    @Override
    protected SecuredNativeSmartRegisterActivity.DefaultOptionsProvider getDefaultOptionsProvider() {
        return new SecuredNativeSmartRegisterActivity.DefaultOptionsProvider() {
            @Override
            public SearchFilterOption searchFilterOption() {
                return null;
            }

            @Override
            public ServiceModeOption serviceMode() {
                return new StockDailyServiceModeOption(null);
            }

            @Override
            public FilterOption villageFilter() {
                return new CursorCommonObjectFilterOption("no village filter", "");
            }

            @Override
            public SortOption sortOption(){ return new DateSort("Reporting Period", "date DESC"); }

            @Override
            public String nameInShortFormForTitle() {
                return Context.getInstance().getStringResource(R.string.stock_register_title);
            }
        };
    }

    @Override
    protected SecuredNativeSmartRegisterActivity.NavBarOptionsProvider getNavBarOptionsProvider() {
        return new SecuredNativeSmartRegisterActivity.NavBarOptionsProvider() {

            @Override
            public DialogOption[] filterOptions() {
                return new DialogOption[]{};
            }

            @Override
            public DialogOption[] serviceModeOptions() {
                return new DialogOption[]{
                        new StockDailyServiceModeOption(null),
                        new StockMonthlyServiceModeOption(null)
                };
            }

            @Override
            public DialogOption[] sortingOptions() {
                return new DialogOption[]{
                        new DateSort("Reporting Period", "date DESC")};
            }

            @Override
            public String searchHint() {
                return Context.getInstance().getStringResource(R.string.str_field_search_hint);
            }
        };
    }

    @Override
    protected void onServiceModeSelection(ServiceModeOption serviceModeOption, View view) {
        super.onServiceModeSelection(serviceModeOption, view);
        add2ndColumnHeaderView();
    }

    protected void add2ndColumnHeaderView() {
        LinearLayout cl = (LinearLayout) mView.findViewById(org.ei.opensrp.R.id.clients_upper_header_layout);
        if (isDailyRegister()){
            cl.getLayoutParams().height = 50;
            cl.setVisibility(View.VISIBLE);
            cl.removeAllViewsInLayout();

            cl.addView(addHeaderItem("", 2));
            cl.addView(addHeaderItem("Vaccine Stock Used or Consumed over the Period", 7));
            cl.addView(addHeaderItem("TOTAL", 2));
        }
        else {
            cl.setVisibility(View.GONE);
            cl.removeAllViewsInLayout();
        }
    }

    private CustomFontTextView addHeaderItem(String text, int weight){
        CustomFontTextView header = new CustomFontTextView(getActivity(), null, org.ei.opensrp.R.style.CustomFontTextViewStyle_ListView_Medium);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, weight);
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        lp.setMargins(3,0,0,2);
        header.setLayoutParams(lp);
        header.setGravity(TEXT_ALIGNMENT_CENTER);
        header.setText(text);
        header.setPadding(weight*3,0,0,4);
        header.setTextSize(20);
        header.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        header.setBackgroundColor(Color.LTGRAY);

        return header;
    }

    protected SmartRegisterClientsProvider clientsProvider() {
        if (isDailyRegister()){
            return new FieldMonitorSmartClientsProvider(
                    getActivity().getApplicationContext(), clientActionHandler, context.alertService(), FieldMonitorSmartClientsProvider.ByMonthByDay.ByDay);
        }
        else {
            return new FieldMonitorSmartClientsProvider(
                    getActivity().getApplicationContext(), clientActionHandler, context.alertService(), FieldMonitorSmartClientsProvider.ByMonthByDay.ByMonth);
        }
    }

    @Override
    protected void onInitialization() {

        mView.findViewById(org.ei.opensrp.R.id.filter_selection).setVisibility(View.GONE);

        mView.findViewById(org.ei.opensrp.R.id.village).setVisibility(View.GONE);

        mView.findViewById(org.ei.opensrp.R.id.sort_selection).setVisibility(View.GONE);

        mView.findViewById(org.ei.opensrp.R.id.sorted_by).setVisibility(View.GONE);

        mView.findViewById(org.ei.opensrp.R.id.label_sorted_by).setVisibility(View.GONE);
    }

    @Override
    protected void startRegistration() {
        HashMap<String, String> overrides = new HashMap<>();
        overrides.putAll(VaccinatorUtils.providerDetails());
        startForm(getRegistrationForm(overrides), "", overrides);
    }

    protected String getRegistrationForm(HashMap<String, String> overridemap) {
        return "vaccine_stock_position";
    }

    @Override
    protected void onResumption() {
        super.onResumption();
        getDefaultOptionsProvider();

        add2ndColumnHeaderView();
    }//end of method

    private List<String> customColumnsDaily = new ArrayList<String>(){{
            add("(select count(*) c from pkwoman where tt1 between t.date and t.date) tt1");
            add("(select count(*) c from pkwoman where tt2 between t.date and t.date) tt2");
            add("(select count(*) c from pkwoman where tt3 between t.date and t.date) tt3");
            add("(select count(*) c from pkwoman where tt4 between t.date and t.date) tt4");
            add("(select count(*) c from pkwoman where tt5 between t.date and t.date) tt5");
            add("(select count(*) c from pkchild where bcg between t.date and t.date) bcg");
            add("(select count(*) c from pkchild where opv0 between t.date and t.date) opv0");
            add("(select count(*) c from pkchild where opv1 between t.date and t.date) opv1");
            add("(select count(*) c from pkchild where opv2 between t.date and t.date) opv2");
            add("(select count(*) c from pkchild where opv3 between t.date and t.date) opv3");
            add("(select count(*) c from pkchild where ipv between t.date and t.date) ipv");
            add("(select count(*) c from pkchild where pcv1 between t.date and t.date) pcv1");
            add("(select count(*) c from pkchild where pcv2 between t.date and t.date) pcv2");
            add("(select count(*) c from pkchild where pcv3 between t.date and t.date) pcv3");
            add("(select count(*) c from pkchild where measles1 between t.date and t.date) measles1");
            add("(select count(*) c from pkchild where measles2 between t.date and t.date) measles2");
            add("(select count(*) c from pkchild where penta1 between t.date and t.date) penta1");
            add("(select count(*) c from pkchild where penta2 between t.date and t.date) penta2");
            add("(select count(*) c from pkchild where penta3 between t.date and t.date) penta3");
    }};

    private List<String> customColumnsMonthly = new ArrayList<String>(){{
        add("(select count(*) c from pkwoman where SUBSTR(tt1,1,7) = SUBSTR(t.date,1,7)) tt1");
        add("(select count(*) c from pkwoman where SUBSTR(tt2,1,7) = SUBSTR(t.date,1,7)) tt2");
        add("(select count(*) c from pkwoman where SUBSTR(tt3,1,7) = SUBSTR(t.date,1,7)) tt3");
        add("(select count(*) c from pkwoman where SUBSTR(tt4,1,7) = SUBSTR(t.date,1,7)) tt4");
        add("(select count(*) c from pkwoman where SUBSTR(tt5,1,7) = SUBSTR(t.date,1,7)) tt5");
        add("(select count(*) c from pkchild where SUBSTR(bcg,1,7) = SUBSTR(t.date,1,7)) bcg");
        add("(select count(*) c from pkchild where SUBSTR(opv0,1,7) = SUBSTR(t.date,1,7)) opv0");
        add("(select count(*) c from pkchild where SUBSTR(opv1,1,7) = SUBSTR(t.date,1,7)) opv1");
        add("(select count(*) c from pkchild where SUBSTR(opv2,1,7) = SUBSTR(t.date,1,7)) opv2");
        add("(select count(*) c from pkchild where SUBSTR(opv3,1,7) = SUBSTR(t.date,1,7)) opv3");
        add("(select count(*) c from pkchild where SUBSTR(ipv,1,7) = SUBSTR(t.date,1,7)) ipv");
        add("(select count(*) c from pkchild where SUBSTR(pcv1,1,7) = SUBSTR(t.date,1,7)) pcv1");
        add("(select count(*) c from pkchild where SUBSTR(pcv2,1,7) = SUBSTR(t.date,1,7)) pcv2");
        add("(select count(*) c from pkchild where SUBSTR(pcv3,1,7) = SUBSTR(t.date,1,7)) pcv3");
        add("(select count(*) c from pkchild where SUBSTR(measles1,1,7) = SUBSTR(t.date,1,7)) measles1");
        add("(select count(*) c from pkchild where SUBSTR(measles2,1,7) = SUBSTR(t.date,1,7)) measles2");
        add("(select count(*) c from pkchild where SUBSTR(penta1,1,7) = SUBSTR(t.date,1,7)) penta1");
        add("(select count(*) c from pkchild where SUBSTR(penta2,1,7) = SUBSTR(t.date,1,7)) penta2");
        add("(select count(*) c from pkchild where SUBSTR(penta3,1,7) = SUBSTR(t.date,1,7)) penta3");
        add("(select sum(total_wasted) c from stock where report='daily' and SUBSTR(date,1,7)=SUBSTR(t.date,1,7)) total_monthly_wasted");
    }};

    @Override
    protected SmartRegisterPaginatedAdapter adapter() {
        setCurrentSearchFilter(new ReportFilterOption(""));
        return new SmartRegisterPaginatedCursorAdapter(getActivity(),
                new SmartRegisterCursorBuilder("stock", isDailyRegister()?"report='daily'":"report='monthly'",
                        "t", (isDailyRegister()?customColumnsDaily:customColumnsMonthly).toArray(new String[]{}), (CursorSortOption) getDefaultOptionsProvider().sortOption())
                , clientsProvider(), SmartRegisterCursorBuilder.DB.DRISHTI);
    }

    private boolean isDailyRegister(){
        return getCurrentServiceModeOption() == null || getCurrentServiceModeOption().name().toLowerCase().contains("dail");
    }

    @Override
    protected void onCreation() {    }

    private class ClientActionHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.stock_detail_holder:
                    if(isDailyRegister()) {
                        DetailActivity.startDetailActivity(getActivity(), (CommonPersonObjectClient) view.getTag(R.id.client_details_tag), FieldMonitorDailyDetailActivity.class);
                    }
                    else {
                        DetailActivity.startDetailActivity(getActivity(), (CommonPersonObjectClient) view.getTag(R.id.client_details_tag), FieldMonitorMonthlyDetailActivity.class);
                    }

                    getActivity().finish();

                    break;
            }
        }
    }
}
