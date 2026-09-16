package com.example.salestracker;

import android.app.*;
import android.os.*;
import android.content.*;
import android.graphics.Color;
import android.text.*;
import android.view.*;
import android.widget.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends Activity {
    EditText item, qty, ubp, usp, discount, customer, address, phone, remarks;
    Spinner seller;
    TextView buying, selling, finalSelling, profit, ruminti, oindrila, date;

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        build();
        calc();
    }

    TextView label(String s) {
        TextView t=new TextView(this);
        t.setText(s); t.setTextSize(14); t.setPadding(0,14,0,5);
        return t;
    }

    EditText input(String hint) {
        EditText e=new EditText(this);
        e.setHint(hint); e.setPadding(12,8,12,8);
        return e;
    }

    TextView autoField() {
        TextView t=new TextView(this);
        t.setTextSize(17); t.setTextColor(Color.rgb(31,78,120));
        t.setTypeface(null,1); t.setPadding(12,10,12,10);
        t.setBackgroundColor(Color.rgb(226,240,217));
        return t;
    }

    void build() {
        ScrollView sc=new ScrollView(this);
        LinearLayout root=new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(24,20,24,30);
        sc.addView(root);

        TextView title=new TextView(this);
        title.setText("Sales Tracker");
        title.setTextSize(28); title.setTypeface(null,1);
        title.setTextColor(Color.rgb(31,78,120));
        root.addView(title);

        TextView sub=new TextView(this);
        sub.setText("Offline • Sales data stays on this phone");
        root.addView(sub);

        root.addView(label("Item Name"));
        item=input("Enter item name"); root.addView(item);

        root.addView(label("Quantity (whole number)"));
        qty=input("1, 2, 3...");
        qty.setInputType(2); root.addView(qty);

        root.addView(label("Seller"));
        seller=new Spinner(this);
        seller.setAdapter(new ArrayAdapter<String>(this,
            android.R.layout.simple_spinner_dropdown_item,
            new String[]{"Ruminti","Oindrila"}));
        root.addView(seller);

        root.addView(label("Unit Buying Price"));
        ubp=input("₹ per unit"); ubp.setInputType(2|8192); root.addView(ubp);

        root.addView(label("Buying Price (automatic)"));
        buying=autoField(); root.addView(buying);

        root.addView(label("Unit Selling Price"));
        usp=input("₹ per unit"); usp.setInputType(2|8192); root.addView(usp);

        root.addView(label("Selling Price (automatic)"));
        selling=autoField(); root.addView(selling);

        root.addView(label("Discount If Any (%)"));
        discount=input("0"); discount.setInputType(2|8192); discount.setText("0"); root.addView(discount);

        root.addView(label("Final Selling Price (automatic)"));
        finalSelling=autoField(); root.addView(finalSelling);

        root.addView(label("Profit (automatic)"));
        profit=autoField(); root.addView(profit);

        root.addView(label("Profit Ruminti (automatic)"));
        ruminti=autoField(); root.addView(ruminti);

        root.addView(label("Profit Oindrila (automatic)"));
        oindrila=autoField(); root.addView(oindrila);

        root.addView(label("Date of Sale"));
        date=new TextView(this);
        date.setText(dateNow()); date.setTextSize(16); date.setPadding(12,14,12,14);
        date.setBackgroundColor(Color.rgb(245,245,245));
        date.setOnClickListener(v -> pickDate());
        root.addView(date);

        root.addView(label("Customer Name"));
        customer=input("Customer name"); root.addView(customer);

        root.addView(label("Customer Address"));
        address=input("Customer address"); address.setMinLines(2); root.addView(address);

        root.addView(label("Customer Phone Number"));
        phone=input("Phone number"); phone.setInputType(3); root.addView(phone);

        root.addView(label("Remarks If Any"));
        remarks=input("Optional remarks"); remarks.setMinLines(2); root.addView(remarks);

        Button save=new Button(this);
        save.setText("SAVE SALE");
        save.setOnClickListener(v -> saveSale());
        root.addView(save);

        TextView rules=new TextView(this);
        rules.setText("\nRules:\nBuying = Quantity × Unit Buying Price\nSelling = Quantity × Unit Selling Price\nFinal Selling = Selling Price − Discount\nProfit = Final Selling Price − Buying Price\nRuminti: 60% / Oindrila: 40%\nOindrila: 60% / Ruminti: 40%");
        root.addView(rules);

        TextWatcher w=new TextWatcher(){
            public void beforeTextChanged(CharSequence s,int a,int c,int d){}
            public void onTextChanged(CharSequence s,int a,int b,int c){calc();}
            public void afterTextChanged(Editable e){}
        };
        qty.addTextChangedListener(w); ubp.addTextChangedListener(w);
        usp.addTextChangedListener(w); discount.addTextChangedListener(w);
        seller.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> p, View v,int pos,long id){calc();}
            public void onNothingSelected(AdapterView<?> p){}
        });

        setContentView(sc);
    }

    String dateNow() {
        return new SimpleDateFormat("dd-MM-yyyy",Locale.getDefault()).format(new Date());
    }

    void pickDate() {
        Calendar c=Calendar.getInstance();
        new DatePickerDialog(this,(v,y,m,d) ->
            date.setText(String.format(Locale.getDefault(),"%02d-%02d-%04d",d,m+1,y)),
            c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH)).show();
    }

    double num(EditText e) {
        try { return Double.parseDouble(e.getText().toString()); }
        catch(Exception x) { return 0; }
    }

    String money(double x) {
        return String.format(Locale.getDefault(),"₹ %.2f",x);
    }

    void calc() {
        double q=num(qty);
        double b=q*num(ubp);
        double s=q*num(usp);
        double d=num(discount);
        double fs=s*(1-d/100.0);
        double p=fs-b;

        buying.setText(money(b));
        selling.setText(money(s));
        finalSelling.setText(money(fs));
        profit.setText(money(p));

        if(seller.getSelectedItemPosition()==0) {
            ruminti.setText(money(p*.60)); oindrila.setText(money(p*.40));
        } else {
            ruminti.setText(money(p*.40)); oindrila.setText(money(p*.60));
        }
    }

    void saveSale() {
        String q=qty.getText().toString().trim();
        if(q.isEmpty() || !q.matches("\\d+")) {
            qty.setError("Whole number required"); return;
        }

        SharedPreferences sp=getSharedPreferences("sales",MODE_PRIVATE);
        int n=sp.getInt("count",0)+1;
        String record=item.getText()+"|"+q+"|"+seller.getSelectedItem()+"|"+
            ubp.getText()+"|"+usp.getText()+"|"+discount.getText()+"|"+
            date.getText()+"|"+customer.getText()+"|"+address.getText()+"|"+
            phone.getText()+"|"+remarks.getText()+"|"+profit.getText();

        sp.edit().putInt("count",n).putString("sale_"+n,record).apply();
        Toast.makeText(this,"Sale saved offline",Toast.LENGTH_SHORT).show();

        item.setText(""); qty.setText(""); ubp.setText(""); usp.setText("");
        discount.setText("0"); customer.setText(""); address.setText("");
        phone.setText(""); remarks.setText("");
    }
}