package org.mbds.mybankapp.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.mbds.mybankapp.R;


public class RegisterFragment extends Fragment {

    private static final String TAG = "param1";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    // The request code must be 0 or greater.
    private static final int PLUS_ONE_REQUEST_CODE = 0;
    // TODO: Rename and change types of parameters
    private int mParam1;
    private String mParam2;
    private String mParam3;

    private TextView mQuery;
    private EditText mAnswer;
    private EditText mAnswer2;
    private Button mButtonNext;

    private OnFragmentInteractionListener mListener;

    public RegisterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param id Parameter 1.
     * @return A new instance of fragment RegisterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterFragment newInstance(int id, String param2, String param3) {
        RegisterFragment fragment = new RegisterFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, id);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getInt(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            mParam3 = getArguments().getString(ARG_PARAM3);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        mQuery = view.findViewById(R.id.next_query);


        mAnswer = view.findViewById(R.id.next_answer);
        mAnswer2 = view.findViewById(R.id.next_answer2);
        mButtonNext = view.findViewById(R.id.next_phone_button);

        if (mParam1 == 1) {
            mQuery.setText("Quel est votre adresse mail ?");
            mAnswer.setHint("Adresse mail");
            mAnswer2.setVisibility(View.GONE);
            mButtonNext.setText(mParam2);
            mButtonNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!mAnswer.getText().toString().equals("")) {
                        onButtonMailPressed(true, mParam1, mAnswer.getText().toString().trim());
                    } else {
                        Toast.makeText(getContext(), "Veillez entrer une adresse", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else if (mParam1 == 2) {
            mQuery.setText("Créer un mot de passe");
            mAnswer.setHint("Mot de passe");
            mAnswer2.setVisibility(View.GONE);
            mButtonNext.setText(mParam2);
            mButtonNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!mAnswer.getText().toString().equals("")) {
                        onButtonPasswordPressed(true, mParam1, mAnswer.getText().toString().trim());
                    } else {
                        Toast.makeText(getContext(), "Veillez entrer un mot de passe", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else if (mParam1 == 3) {
            mQuery.setText("Quel est votre nom ?");
            mAnswer.setHint("Prénom");
            mAnswer2.setHint("Nom de famille");
            mAnswer2.setVisibility(View.VISIBLE);
            mButtonNext.setText(mParam2);
            mButtonNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!mAnswer.getText().toString().equals("") && !mAnswer2.getText().toString().equals("")) {
                        onButtonNamePressed(true, mAnswer.getText().toString().trim(), mAnswer2.getText().toString().trim());
                    } else {
                        Toast.makeText(getContext(), "Veillez entrer un nom et prenom", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else if (mParam1 == 4) {
            mQuery.setText("Terminer la création du compte");
            mAnswer.setVisibility(View.GONE);
            mAnswer2.setVisibility(View.GONE);
            mButtonNext.setText(mParam2);
            mButtonNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onButtonSignUpPressed(true);
                }
            });
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Refresh the state of the +1 button each time the activity receives focus.
//        mPlusOneButton.initialize(PLUS_ONE_URL, PLUS_ONE_REQUEST_CODE);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonMailPressed(Boolean b, int id,  String mail) {
        if (mListener != null) {
            mListener.clickMailToPassword(b);
            mListener.setData(id, mail);
        }
    }

    public void onButtonPasswordPressed(Boolean b, int id, String password) {
        if (mListener != null) {
            mListener.clickPasswordToName(b);
            mListener.setData(id, password);
        }
    }

    public void onButtonNamePressed(Boolean b, String firstName, String lastName) {
        if (mListener != null) {
            mListener.clickNameToSignUp(b);
            mListener.setData2(firstName, lastName);
        }
    }

    public void onButtonSignUpPressed(Boolean b) {
        if (mListener != null) {
            mListener.clickSignUp(b);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void clickMailToPassword(Boolean b);
        void clickPasswordToName(Boolean b);
        void clickNameToSignUp(Boolean b);
        void clickSignUp(Boolean b);

        void setData(int idFrag, String value);
        void setData2(String value1, String value2);
    }

}
