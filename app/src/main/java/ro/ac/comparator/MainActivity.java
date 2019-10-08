package ro.ac.comparator;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

import androidx.constraintlayout.widget.ConstraintLayout;

public class MainActivity extends Activity {
	private EditText textPrice1, textPrice2, textQuantity1, textQuantity2;
	private TextView labelProduct1, labelProduct2, labelPricePerUnit1, labelPricePerUnit2, labelTextQuantity1, labelTextQuantity2;
	private Spinner spinnerUnit1, spinnerUnit2;
	private int green, red, blue;
	private ConstraintLayout layout;
	String[] units;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		green = getResources().getColor(R.color.green);
		red = getResources().getColor(R.color.red);
		blue = getResources().getColor(R.color.blue);

		textPrice1 = findViewById(R.id.textPrice1);
		textPrice2 = findViewById(R.id.textPrice2);
		textQuantity1 = findViewById(R.id.textQuantity1);
		textQuantity2 = findViewById(R.id.textQuantity2);

		labelPricePerUnit1 = findViewById(R.id.labelPricePerUnit1);
		labelPricePerUnit2 = findViewById(R.id.labelPricePerUnit2);
		labelTextQuantity1 = findViewById(R.id.labelTextQuantity1);
		labelTextQuantity2 = findViewById(R.id.labelTextQuantity2);
		labelProduct1 = findViewById(R.id.labelProduct1);
		labelProduct2 = findViewById(R.id.labelProduct2);

		spinnerUnit1 = findViewById(R.id.spinnerUnit1);
		spinnerUnit2 = findViewById(R.id.spinnerUnit2);
		labelProduct1.setTextColor(blue);
		labelProduct2.setTextColor(blue);

		layout = findViewById(R.id.layout);
		units = getResources().getStringArray(R.array.units);

		spinnerUnit1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				labelTextQuantity1.setText(units[spinnerUnit1.getSelectedItemPosition()]);
				labelPricePerUnit1.setText("");
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {

			}
		});

		spinnerUnit2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				labelTextQuantity2.setText(units[spinnerUnit2.getSelectedItemPosition()]);
				labelPricePerUnit2.setText("");
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {

			}
		});
	}

	private int calculate(double price1, double quantity1, double price2, double quantity2) {
		double raport1 = quantity1 / price1;
		double raport2 = quantity2 / price2;

		int result;
		if (raport1 > raport2) {
			result = 1;
		} else if (raport1 < raport2) {
			result = 2;
		} else {
			result = 0;
		}

		return result;
	}

	public void onCalculate(View view) {
		//get text values
		String valueTextPrice1 = textPrice1.getText().toString();
		String valueTextPrice2 = textPrice2.getText().toString();
		String valueTextQuantity1 = textQuantity1.getText().toString();
		String valueTextQuantity2 = textQuantity2.getText().toString();

		double price1;
		double quantity1;
		double price2;
		double quantity2;

		try {
			//get field values
			price1 = (valueTextPrice1.equals("0") || valueTextPrice1.isEmpty()) ? 0 : Double.parseDouble(valueTextPrice1);
			quantity1 = (valueTextQuantity1.equals("0") || valueTextQuantity1.isEmpty()) ? 0 : Double.parseDouble(valueTextQuantity1);
			price2 = (valueTextPrice2.equals("0") || valueTextPrice2.isEmpty()) ? 0 : Double.parseDouble(valueTextPrice2);
			quantity2 = (valueTextQuantity2.equals("0") || valueTextQuantity2.isEmpty()) ? 0 : Double.parseDouble(valueTextQuantity2);

			if (price1 != 0 && quantity1 != 0) {
				labelPricePerUnit1.setText(pricePerUnit(price1, quantity1, spinnerUnit1.getSelectedItemPosition()));
			}
			if (price2 != 0 && quantity2 != 0) {
				labelPricePerUnit2.setText(pricePerUnit(price2, quantity2, spinnerUnit2.getSelectedItemPosition()));
			}
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), "Eroare", Toast.LENGTH_LONG).show();
			return;
		}

		//check for 0 or empty
		for (String item : new String[]{
				valueTextPrice1,
				valueTextPrice2,
				valueTextQuantity1,
				valueTextQuantity2}) {
			if (item.equals("0") || item.isEmpty()) {
				Toast.makeText(getApplicationContext(), "Pentru comparație, toate câmpurile trebuie completate cu valori diferite de 0!", Toast.LENGTH_LONG).show();
				return;
			}
		}

		//send values to calculate
		//use the return result to format the labels
		int result = calculate(price1, price2, quantity1, quantity2);

		switch (result) {
			case 0:
				labelProduct1.setTextColor(blue);
				labelProduct2.setTextColor(blue);
				Toast.makeText(getApplicationContext(), "Ambele produse au același cost", Toast.LENGTH_LONG).show();
				break;
			case 1:
				labelProduct1.setTextColor(green);
				labelProduct2.setTextColor(red);
				Toast.makeText(getApplicationContext(), "Produsul 1 este mai ieftin decât Produsul 2", Toast.LENGTH_LONG).show();
				break;
			case 2:
				labelProduct1.setTextColor(red);
				labelProduct2.setTextColor(green);
				Toast.makeText(getApplicationContext(), "Produsul 2 este mai ieftin decât Produsul 1", Toast.LENGTH_LONG).show();
				break;
		}
	}

	public void onClear(View view) {
		//reformat the labels
		labelProduct1.setTextColor(blue);
		labelProduct2.setTextColor(blue);

		//restore spinners
		spinnerUnit1.setSelection(0);
		spinnerUnit2.setSelection(0);

		//empty inputs
		textPrice1.setText("");
		textPrice2.setText("");
		textQuantity1.setText("");
		textQuantity2.setText("");

		labelPricePerUnit1.setText("");
		labelPricePerUnit2.setText("");
		String defaultQuantityLabel = units[spinnerUnit1.getSelectedItemPosition()];
		labelTextQuantity1.setText(defaultQuantityLabel);
		labelTextQuantity2.setText(defaultQuantityLabel);

		layout.requestFocus();
	}

	protected String pricePerUnit(double price, double quantity, int spinnerItem) {
		//spinnerItem is the int index of the units array: unitate, g, kg, ml, l
		String result = "";

		if (quantity == 0) {
			Toast.makeText(getApplicationContext(), "Cantitatea nu poate fi 0!", Toast.LENGTH_LONG).show();
			return result;
		}

		switch (spinnerItem) {
			case 0:
				//u. -> price per unit
				result = formatDoubleAsString(price / quantity) + " lei/unit.";
				break;

			case 1:
				//g -> price per kg
				result = formatDoubleAsString((price * 1000) / quantity) + " lei/kg";
				break;

			case 2:
				//kg -> price per kg
				result = formatDoubleAsString(price / quantity) + " lei/kg";
				break;

			case 3:
				//ml -> price per l
				result = formatDoubleAsString((price * 1000) / quantity) + " lei/l";
				break;

			case 4:
				//l -> price per l
				result = formatDoubleAsString(price / quantity) + " lei/l";
				break;

			default:
				break;
		}

		return result;
	}

	protected static String formatDoubleAsString(double value) {
		NumberFormat numberFormat = NumberFormat.getInstance(new Locale("ro", "RO"));
		numberFormat.setRoundingMode(RoundingMode.HALF_EVEN);
		numberFormat.setMinimumFractionDigits(2);
		numberFormat.setMaximumFractionDigits(2);
		return numberFormat.format(value);
	}
}