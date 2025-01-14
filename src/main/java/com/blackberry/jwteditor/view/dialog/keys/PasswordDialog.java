/*
Author : Fraser Winterborn

Copyright 2021 BlackBerry Limited

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package com.blackberry.jwteditor.view.dialog.keys;

import com.blackberry.jwteditor.model.keys.Key;
import com.blackberry.jwteditor.model.keys.PasswordKey;
import com.blackberry.jwteditor.presenter.PresenterStore;
import com.blackberry.jwteditor.view.utils.DocumentAdapter;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.UUID;

/**
 * "New Password" dialog for Keys tab
 */
public class PasswordDialog extends KeyDialog {
    private static final String TITLE_RESOURCE_ID = "password";

    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField textFieldPassword;
    private JSpinner spinnerSaltLength;
    private JSpinner spinnerIterations;
    private JTextField textFieldKeyId;

    private PasswordKey key;

    public PasswordDialog(Window parent, PresenterStore presenters){
        this(parent, presenters, UUID.randomUUID().toString(), "", 8, 1000);
        originalId = null;
    }

    public PasswordDialog(Window parent, PresenterStore presenters, PasswordKey key){
        this(parent, presenters, key.getID(), key.getPassword(), key.getSaltLength(), key.getIterations());
    }

    private PasswordDialog(Window parent, PresenterStore presenters, String keyId, String password, int saltLength, int iterations) {
        super(parent, TITLE_RESOURCE_ID);
        this.presenters = presenters;
        originalId = keyId;

        setContentPane(contentPane);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());
        buttonCancel.addActionListener(e -> onCancel());

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        // Initialise the iterations spinner
        SpinnerNumberModel spinnerModelIterations = new SpinnerNumberModel();
        spinnerModelIterations.setStepSize(1000);
        spinnerModelIterations.setMinimum(1);
        spinnerIterations.setModel(spinnerModelIterations);

        // Initialise the salt length spinner
        SpinnerNumberModel spinnerModeSalt = new SpinnerNumberModel();
        spinnerModeSalt.setMinimum(1);
        spinnerSaltLength.setModel(spinnerModeSalt);

        DocumentListener documentListener = new DocumentAdapter(e -> checkInput());

        // Attach event handlers for the inputs changing
        textFieldPassword.getDocument().addDocumentListener(documentListener);
        textFieldKeyId.getDocument().addDocumentListener(documentListener);
        spinnerSaltLength.addChangeListener(e -> checkInput());
        spinnerIterations.addChangeListener(e -> checkInput());

        // Set the inputs to their initial values, triggering the event handlers to check the input
        textFieldKeyId.setText(keyId);
        textFieldPassword.setText(password);
        spinnerModeSalt.setValue(saltLength);
        spinnerModelIterations.setValue(iterations);
    }

    /**
     * Event handler for checking the inputs are valid when any values change
     */
    private void checkInput() {
        // Check that the password and key id values are set, enable/disable OK based on the result
        buttonOK.setEnabled(textFieldKeyId.getText().length() > 0 && textFieldPassword.getText().length() > 0);
    }

    /**
     * OK clicked, build a PasswordKey from the form values
     */
    @Override
    void onOK() {
        key = new PasswordKey(textFieldKeyId.getText(), textFieldPassword.getText(), (Integer) spinnerSaltLength.getValue(), (Integer) spinnerIterations.getValue());
        super.onOK();
    }

    /**
     * Called when the Cancel or X button is pressed. Set the changed key to null and destroy the window
     */
    @Override
    void onCancel() {
        key = null;
        dispose();
    }

    /**
     * Get the new/modified key resulting from the operations of this dialog
     * @return the new/modified JWK
     */
    @Override
    public Key getKey() {
        return key;
    }
}
