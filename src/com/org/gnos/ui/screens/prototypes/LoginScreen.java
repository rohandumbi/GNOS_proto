package com.org.gnos.ui.screens.prototypes;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;


public class LoginScreen extends Composite {
	private Text textUserName;
	private Text textPassword;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public LoginScreen(Composite parent, int style) {
		super(parent, style);
		setFont(SWTResourceManager.getFont("Calibri", 12, SWT.NORMAL));
		Layout formLayout = new FormLayout();
		setLayout(formLayout);
		
		Label lblUsername = new Label(this, SWT.NONE);
		lblUsername.setFont(SWTResourceManager.getFont("Segoe UI", 14, SWT.NORMAL));
		FormData fd_lblUsername = new FormData();
		fd_lblUsername.top = new FormAttachment(40);
		fd_lblUsername.left = new FormAttachment(32);
		lblUsername.setLayoutData(fd_lblUsername);
		lblUsername.setText("Username:");
		
		textUserName = new Text(this, SWT.BORDER);
		FormData fd_textUserName = new FormData();
		fd_textUserName.top = new FormAttachment(40);
		fd_textUserName.right = new FormAttachment(lblUsername, 405, SWT.RIGHT);
		fd_textUserName.left = new FormAttachment(lblUsername, 16);
		textUserName.setLayoutData(fd_textUserName);
		
		Label lblPassword = new Label(this, SWT.NONE);
		lblPassword.setFont(SWTResourceManager.getFont("Segoe UI", 14, SWT.NORMAL));
		FormData fd_lblPassword= new FormData();
		fd_lblPassword.top = new FormAttachment(50);
		fd_lblPassword.left = new FormAttachment(32);
		lblPassword.setLayoutData(fd_lblPassword);
		lblPassword.setText("Password:");
		
		textPassword = new Text(this, SWT.BORDER);
		FormData fd_textPassword = new FormData();
		fd_textPassword.top = new FormAttachment(50);
		fd_textPassword.right = new FormAttachment(lblPassword, 411, SWT.RIGHT);
		fd_textPassword.left = new FormAttachment(textUserName, 0, SWT.LEFT);
		textPassword.setLayoutData(fd_textPassword);
		
		Button loginButton = new Button(this, SWT.NONE);
		loginButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				//TODO action for login button click
			}
		});
		loginButton.setFont(SWTResourceManager.getFont("Segoe UI", 14, SWT.NORMAL));
		FormData fd_loginButton = new FormData();
		//fd_loginButton.bottom = new FormAttachment(textPassword, 83, SWT.BOTTOM);
		fd_loginButton.top = new FormAttachment(textPassword, 46);
		fd_loginButton.right = new FormAttachment(textPassword, -80, SWT.RIGHT);
		fd_loginButton.left = new FormAttachment(textPassword, -20, SWT.LEFT);
		loginButton.setLayoutData(fd_loginButton);
		loginButton.setText("Login");
		
		Label labelLogo = new Label(this, SWT.NONE);
		labelLogo.setImage(SWTResourceManager.getImage(LoginScreen.class, "/com/org/gnos/resources/logo.png"));
		//labelLogo.setImage(logoImage);
		//FormData fd_lblNewLabel = new FormData(SWT.CENTER, SWT.CENTER);
		int offset = -labelLogo.computeSize(SWT.DEFAULT, SWT.DEFAULT).x / 2;
		
		FormData fd_logo = new FormData();
		/*fd_lblNewLabel.bottom = new FormAttachment(0, 170);
		fd_lblNewLabel.right = new FormAttachment(0, 604);
		fd_lblNewLabel.top = new FormAttachment(0, 74);*/
		/*fd_lblNewLabel.top = new FormAttachment(10);
		fd_lblNewLabel.left = new FormAttachment(lblUsername, 40, SWT.LEFT);*/
		fd_logo.left = new FormAttachment(50,offset);
		fd_logo.bottom = new FormAttachment(textUserName, -10, SWT.TOP);
		labelLogo.setLayoutData(fd_logo);

	}

	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
