package com.valyakinaleksey.followplan.presentation.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.Iconics;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.valyakinaleksey.followplan.R;
import com.valyakinaleksey.followplan.data.DatabaseHelper;
import com.valyakinaleksey.followplan.domain.adapters.SpinnerToolbarAdapter;
import com.valyakinaleksey.followplan.domain.adapters.TasksArrayAdapter;
import com.valyakinaleksey.followplan.domain.receivers.MainReceiver;
import com.valyakinaleksey.followplan.presentation.dialogs.PlansDialogFragment;
import com.valyakinaleksey.followplan.presentation.fragments.PlanFragment;
import com.valyakinaleksey.followplan.presentation.model.Plan;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.LinkedList;
import java.util.List;

import static com.valyakinaleksey.followplan.util.Constants.LOG_TAG;


public class MainActivity extends AppCompatActivity {
    public static final int ALL_TASKS_ID = 1;
    public static final int TODAY_ID = 3;
    public static final int PLANS_ID = 4;
    public static final int FILTERS_ID = 5;
    public static final int REPORT_BUG_ID = 7;
    public static final int SETTINGS_ID = 8;
    public static final int REQUEST_CODE_SEARCH = 10;
    private static final String CURRENT_FRAGMENT_POSITION = "position";
    private static final String CURRENT_TITLE = "title";

    private int currentFragmentPosition = 1;
    private Toolbar mainToolBar;
    private FragmentManager fragmentManager;
    private Drawer drawer;
    private SecondaryDrawerItem drawerItemPlans;
    private Spinner mainToolbarSpinner;
    private SpinnerToolbarAdapter spinnerAdapter;
    private FloatingActionButton floatingActionButton;
    private Fragment currentFragment;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolbarAdapter();
        initActivity(savedInstanceState);
        initDrawer();
    }


    public void updateProjectsCount() {
        drawerItemPlans.withBadge("" + Plan.getPlans().size());
        drawer.updateItem(drawerItemPlans);
    }

    private void initActivity(Bundle savedInstanceState) {
        fragmentManager = getSupportFragmentManager();
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        if (savedInstanceState == null) {
            JodaTimeAndroid.init(this);
            MainReceiver.checkPendingIntentExist(getBaseContext());
            final DateTimeZone aDefault = DateTimeZone.getDefault();
            Log.d(LOG_TAG, aDefault.toString());
            Log.d(LOG_TAG, DateTime.now(aDefault).toString());
            Iconics.init(getApplicationContext());
            Iconics.registerFont(new FontAwesome());
        }
        if (Plan.getPlans().isEmpty()) {
            DatabaseHelper.getInstance(getBaseContext()).initFromDb();
        }
        restoreSavedInstanceState(savedInstanceState);
        initToolBar();
    }

    private void restoreSavedInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            createFragment(getString(R.string.tasks));
        } else {
            currentFragmentPosition = savedInstanceState.getInt(CURRENT_FRAGMENT_POSITION);
            title = savedInstanceState.getString(CURRENT_TITLE);
        }
    }

    private Account getAccount() {
        AccountManager manager = AccountManager.get(this);
        Account[] accounts = manager.getAccountsByType("com.google");
        List<String> possibleEmails = new LinkedList<>();

        for (Account account : accounts) {
            possibleEmails.add(account.name);
        }
        if (!possibleEmails.isEmpty() && possibleEmails.get(0) != null) {
            String email = possibleEmails.get(0);
            String[] parts = email.split("@");
            if (parts.length > 0 && parts[0] != null)
                return accounts[0];
            else
                return null;
        } else
            return null;
    }

    public void createFragment(Fragment fragment, String fragmentName) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.fragment_container, fragment, fragmentName);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
        currentFragment = fragment;
        title = fragmentName;
        spinnerAdapter.setTitle(fragmentName);
    }

    public void createFragment(String fragmentName) {
        Fragment fragment = fragmentManager.findFragmentByTag(fragmentName);
        if (fragment == null) {
            fragment = getFragment(fragmentName);
        }
        createFragment(fragment, fragmentName);
    }

    private Fragment getFragment(String fragmentName) {
        Fragment fragment = null;
        if (fragmentName.equals(getString(R.string.tasks))) {
            fragment = PlanFragment.newInstance(PlanFragment.TASKS_ALL);
        } else if (fragmentName.equals(getString(R.string.today))) {
            fragment = PlanFragment.newInstance(PlanFragment.TASKS_TODAY);
        }
        return fragment;
    }

    private void initToolBar() {
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mainToolBar = toolbar;
        mainToolBar.findViewById(R.id.search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getBaseContext(), SearchableActivity.class), REQUEST_CODE_SEARCH);
            }
        });
        mainToolbarSpinner = ((Spinner) mainToolBar.findViewById(R.id.toolbar_spinner));
        if (!TextUtils.isEmpty(title)) {
            spinnerAdapter.setTitle(title);
        }
        mainToolbarSpinner.setAdapter(spinnerAdapter);
    }

    private void initToolbarAdapter() {
        String allTasks = getString(R.string.all_tasks);
        String unCompletedTasks = getString(R.string.uncompleted_tasks);
        String completedTasks = getString(R.string.completed_tasks);
        spinnerAdapter = new SpinnerToolbarAdapter(getBaseContext(), R.layout.spinner_toolbar_item, R.id.title,
                new String[]{allTasks, unCompletedTasks, completedTasks});
    }

    public void initSpinnerFilter(final TasksArrayAdapter tasksArrayAdapter) {
        mainToolbarSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String charSequence = "";
                switch (i) {
                    case 1:
                        charSequence = TasksArrayAdapter.UNCOMPLETED_TASKS;
                        break;
                    case 2:
                        charSequence = TasksArrayAdapter.COMPLETED_TASKS;
                        break;
                }
                tasksArrayAdapter.getFilter().filter(charSequence);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return id == R.id.action_notifications || super.onOptionsItemSelected(item);
    }

    public void updateFragment() {
        if (currentFragment instanceof PlanFragment && currentFragment.isVisible()) {
            final Plan plan = ((PlanFragment) currentFragment).getPlan();
            if (plan != null) {
                if (!Plan.getPlans().containsKey(plan.getId())) {
                    createFragment(getString(R.string.tasks));
                }
            } else {
                createFragment(getFragment(currentFragment.getTag()), currentFragment.getTag());
            }
        }
    }

    public FloatingActionButton getFloatingActionButton() {
        return floatingActionButton;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_FRAGMENT_POSITION, currentFragmentPosition);
        outState.putString(CURRENT_TITLE, title);
    }

    private void initDrawer() {
        Account account = getAccount();
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(
                        new ProfileDrawerItem().withEmail(account == null ? "User" : account.name).withIcon(FontAwesome.Icon.faw_user)
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();
        drawerItemPlans = new SecondaryDrawerItem().withName(R.string.plans).withIcon(FontAwesome.Icon.faw_calendar_check_o).withBadge(""
                + Plan.getPlans().size()).withSelectable(false).withIdentifier(PLANS_ID);
        drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(mainToolBar)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.tasks).withIcon(FontAwesome.Icon.faw_home).withIdentifier(ALL_TASKS_ID),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName(R.string.today).withIcon(FontAwesome.Icon.faw_calendar_o).withIdentifier(TODAY_ID),
                        drawerItemPlans,
                        new SecondaryDrawerItem().withName(R.string.filters).withIcon(FontAwesome.Icon.faw_filter).withIdentifier(FILTERS_ID),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName(R.string.report_bug).withIcon(FontAwesome.Icon.faw_bug).withSelectable(false),
                        new SecondaryDrawerItem().withName(R.string.settings).withIcon(FontAwesome.Icon.faw_gear).withSelectable(false)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                        switch (position) {
                            case ALL_TASKS_ID:
                                createFragment(getString(R.string.tasks));
                                break;
                            case TODAY_ID:
                                createFragment(getString(R.string.today));
                                break;
                            case PLANS_ID:
                                PlansDialogFragment.show(MainActivity.this);
                                break;
                            case FILTERS_ID:
                                break;
                            case REPORT_BUG_ID:
                                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                                emailIntent.setType("text/plain");
                                emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.error_title));
                                emailIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.hello) + "\n");
                                emailIntent.setData(Uri.parse("mailto:xzaleksey@gmail.com"));
                                emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                try {
                                    startActivity(emailIntent);
                                } catch (ActivityNotFoundException ex) {
                                    Toast.makeText(getBaseContext(), getString(R.string.no_email_clients), Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case SETTINGS_ID:
                                startActivity(new Intent(MainActivity.this, MyPreferenceActivity.class));
                                break;
                        }
                        if (position != PLANS_ID && position != SETTINGS_ID) {
                            currentFragmentPosition = position;
                        }
                        return false;
                    }
                })
                .build();
        drawer.setSelection(currentFragmentPosition, false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SEARCH) {

            fragmentManager.beginTransaction()
                    .detach(currentFragment)
                    .attach(currentFragment)
                    .commitAllowingStateLoss();
        }
        Log.d(LOG_TAG, "onActivityResult");
    }
}

