package com.org.gnos.ui.custom.controls;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.org.gnos.core.ProjectConfigutration;
import com.org.gnos.db.model.Pit;
import com.org.gnos.db.model.PitGroup;
import com.org.gnos.db.model.Stockpile;

public class StockpileDefinitionGrid extends Composite {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	private Composite compositeGridHeader;
	private List<Composite> allRows;
	private Composite presentRow;
	private Label firstSeparator;
	private Label secondSeparator;
	private Label thirdSeparator;
	private Label fourthSeparator;
	private Label fifthSeparator;
	private Label sixthSeparator;
	private Label seventhSeparator;

	private int pitEndIndex = 0;
	private int pitGroupEndIndex = 0;
	
	private List<Stockpile> stockpileList;
	private final int FIRST_SEPARATOR_POSITION = 10;
	private final int SECOND_SEPARATOR_POSITION = 20;
	private final int THIRD_SEPARATOR_POSITION = 35;
	private final int FOURTH_SEPARATOR_POSITION = 55;
	private final int FIFTH_SEPARATOR_POSITION = 60;
	private final int SIXTH_SEPARATOR_POSITION = 70;
	private final int SEVENTH_SEPARATOR_POSITION = 75;
	
	private ProjectConfigutration projectConfiguration ;

	public StockpileDefinitionGrid(Composite parent, int style) {
		super(parent, style);
		this.allRows = new ArrayList<Composite>();
		this.projectConfiguration = ProjectConfigutration.getInstance();
		this.stockpileList = projectConfiguration.getStockPileList();
		this.createContent(parent);
	}

	private void createContent(Composite parent){
		this.setLayout(new FormLayout());
		this.createHeader();
		for(Stockpile stockpile: this.stockpileList){
			this.addRow(stockpile);
		}
	}

	private String[] getPits(){

		ProjectConfigutration projectConfigutration = ProjectConfigutration.getInstance();
		List<Pit> pits = projectConfigutration.getPitList();
		String[] comboItems = new String[pits.size()];
		for(int i=0; i < pits.size(); i++){
			comboItems[i] = pits.get(i).getPitName();
		}
		return comboItems;
	}
	
	private String[] getPitGroups(){
		ProjectConfigutration projectConfigutration = ProjectConfigutration.getInstance();
		List<PitGroup> pits = projectConfigutration.getPitGroupList();
		String[] comboItems = new String[pits.size()];
		for(int i=0; i < pits.size(); i++){
			comboItems[i] = pits.get(i).getName();
		}
		return comboItems;
	}
	
	private void createHeader(){
		compositeGridHeader = new Composite(this, SWT.BORDER);
		compositeGridHeader.setBackground(SWTResourceManager.getColor(230, 230, 230));
		compositeGridHeader.setLayout(new FormLayout());
		FormData fd_compositeGridHeader = new FormData();
		fd_compositeGridHeader.bottom = new FormAttachment(0, 31);
		fd_compositeGridHeader.top = new FormAttachment(0);
		fd_compositeGridHeader.left = new FormAttachment(0);
		fd_compositeGridHeader.right = new FormAttachment(100);
		compositeGridHeader.setLayoutData(fd_compositeGridHeader);

		Label lblDumpType = new Label(compositeGridHeader, SWT.NONE);
		lblDumpType.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		FormData fd_lblDumpType = new FormData();
		fd_lblDumpType.top = new FormAttachment(0,2);
		fd_lblDumpType.left = new FormAttachment(0, 10);
		lblDumpType.setLayoutData(fd_lblDumpType);
		lblDumpType.setText("External/Inpit Stcokpile");
		lblDumpType.setBackground(SWTResourceManager.getColor(230, 230, 230));

		firstSeparator = new Label(compositeGridHeader, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_firstSeparator = new FormData();
		fd_firstSeparator.left = new FormAttachment(FIRST_SEPARATOR_POSITION);
		firstSeparator.setLayoutData(fd_firstSeparator);
		
		Label lblDumpName = new Label(compositeGridHeader, SWT.NONE);
		lblDumpName.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		FormData fd_lblDumpName = new FormData();
		fd_lblDumpName.top = new FormAttachment(0,2);
		fd_lblDumpName.left = new FormAttachment(firstSeparator, 35);
		lblDumpName.setLayoutData(fd_lblDumpName);
		lblDumpName.setText("Stockiple Name");
		lblDumpName.setBackground(SWTResourceManager.getColor(230, 230, 230));
		
		secondSeparator = new Label(compositeGridHeader, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_secondSeparator = new FormData();
		fd_secondSeparator.left = new FormAttachment(SECOND_SEPARATOR_POSITION);
		secondSeparator.setLayoutData(fd_secondSeparator);
		
		Label lblPitGroup = new Label(compositeGridHeader, SWT.NONE);
		lblPitGroup.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		FormData fd_lblFirstPitAssociatedBench = new FormData();
		fd_lblFirstPitAssociatedBench.top = new FormAttachment(0,2);
		fd_lblFirstPitAssociatedBench.left = new FormAttachment(secondSeparator, 80);
		lblPitGroup.setLayoutData(fd_lblFirstPitAssociatedBench);
		lblPitGroup.setText("Pit/Pit Group");
		lblPitGroup.setBackground(SWTResourceManager.getColor(230, 230, 230));
		
		thirdSeparator = new Label(compositeGridHeader, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_thirdSeparator = new FormData();
		fd_thirdSeparator.left = new FormAttachment(THIRD_SEPARATOR_POSITION);
		thirdSeparator.setLayoutData(fd_thirdSeparator);
		
		Label lblExpression = new Label(compositeGridHeader, SWT.NONE);
		lblExpression.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		FormData fd_lblExpression = new FormData();
		fd_lblExpression.top = new FormAttachment(0,2);
		fd_lblExpression.left = new FormAttachment(thirdSeparator, 110);
		lblExpression.setLayoutData(fd_lblExpression);
		lblExpression.setText("Expression");
		lblExpression.setBackground(SWTResourceManager.getColor(230, 230, 230));
		
		fourthSeparator = new Label(compositeGridHeader, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_fourthSeparator = new FormData();
		fd_fourthSeparator.left = new FormAttachment(FOURTH_SEPARATOR_POSITION);
		fourthSeparator.setLayoutData(fd_fourthSeparator);
		
		Label lblHasCapacity = new Label(compositeGridHeader, SWT.NONE);
		lblHasCapacity.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		FormData fd_lblHasCapacity = new FormData();
		fd_lblHasCapacity.top = new FormAttachment(0,2);
		fd_lblHasCapacity.left = new FormAttachment(fourthSeparator, 2);
		lblHasCapacity.setLayoutData(fd_lblHasCapacity);
		lblHasCapacity.setText("has capacity");
		lblHasCapacity.setBackground(SWTResourceManager.getColor(230, 230, 230));
		
		fifthSeparator = new Label(compositeGridHeader, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_fifthSeparator = new FormData();
		fd_fifthSeparator.left = new FormAttachment(FIFTH_SEPARATOR_POSITION);
		fifthSeparator.setLayoutData(fd_fifthSeparator);
		
		Label lblCapacity = new Label(compositeGridHeader, SWT.NONE);
		lblCapacity.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		FormData fd_lblCapacity = new FormData();
		fd_lblCapacity.top = new FormAttachment(0,2);
		fd_lblCapacity.left = new FormAttachment(fifthSeparator, 60);
		lblCapacity.setLayoutData(fd_lblCapacity);
		lblCapacity.setText("Capacity");
		lblCapacity.setBackground(SWTResourceManager.getColor(230, 230, 230));
		
		sixthSeparator = new Label(compositeGridHeader, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_sixthSeparator = new FormData();
		fd_sixthSeparator.left = new FormAttachment(SIXTH_SEPARATOR_POSITION);
		sixthSeparator.setLayoutData(fd_sixthSeparator);
		
		Label lblReclaim = new Label(compositeGridHeader, SWT.NONE);
		lblReclaim.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		FormData fd_lblReclaim = new FormData();
		fd_lblReclaim.top = new FormAttachment(0,2);
		fd_lblReclaim.left = new FormAttachment(sixthSeparator, 15);
		lblReclaim.setLayoutData(fd_lblReclaim);
		lblReclaim.setText("reclaim");
		lblReclaim.setBackground(SWTResourceManager.getColor(230, 230, 230));
		
		seventhSeparator = new Label(compositeGridHeader, SWT.SEPARATOR | SWT.VERTICAL);
		FormData fd_seventhSeparator = new FormData();
		fd_seventhSeparator.left = new FormAttachment(SEVENTH_SEPARATOR_POSITION);
		seventhSeparator.setLayoutData(fd_seventhSeparator);
		
		this.presentRow = this.compositeGridHeader;//referring to the header as the 1st row when there are no rows inserted yet
	}
	
	private void createStockpileNameWidget(Composite rowComposite,Composite stockpileNameComposite, int type){
		for (Control control : stockpileNameComposite.getChildren()) {
	        control.dispose();
	    }
		final Control stockpileNameNameWidget;
		final Stockpile stockpile = (Stockpile)rowComposite.getData();
		String stockpileName = stockpile.getName();
		if(type == 0){
			stockpileNameNameWidget = new Text(stockpileNameComposite, SWT.NONE);
			if(stockpileName != null){
				((Text)stockpileNameNameWidget).setText(stockpileName);
			}
			((Text)stockpileNameNameWidget).addModifyListener(new ModifyListener(){
				public void modifyText(ModifyEvent event) {
					stockpile.setName(((Text)stockpileNameNameWidget).getText());
				}
			});
		}else{
			stockpileNameNameWidget = new Combo(stockpileNameComposite, SWT.NONE);
			((Combo) stockpileNameNameWidget).setItems(getPits());
			if(stockpileName != null){
				((Combo)stockpileNameNameWidget).setText(stockpileName);
			}
			((Combo) stockpileNameNameWidget).addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					stockpile.setName(((Combo)stockpileNameNameWidget).getText());
				}
			});
		}
		stockpileNameComposite.layout();
	}

	public void addRow(final Stockpile stockpile){
		final Composite compositeRow = new Composite(this, SWT.BORDER);
		compositeRow.setLayout(new FormLayout());
		Color backgroundColor = SWTResourceManager.getColor(SWT.COLOR_WHITE);
		compositeRow.setData(stockpile);
		if((this.allRows != null) && (this.allRows.size()%2 != 0)){
			backgroundColor =  SWTResourceManager.getColor(245, 245, 245);
		}
		compositeRow.setBackground(backgroundColor);
		FormData fd_compositeRow = new FormData();
		fd_compositeRow.left = new FormAttachment(this.presentRow, 0, SWT.LEFT);
		//fd_compositeRow.bottom = new FormAttachment(this.presentRow, 26, SWT.BOTTOM);
		fd_compositeRow.right = new FormAttachment(this.presentRow, 0, SWT.RIGHT);
		fd_compositeRow.top = new FormAttachment(this.presentRow, 0, SWT.BOTTOM);
		
		final Combo comboDumpType = new Combo(compositeRow, SWT.NONE);
		String[] accociatedTypes = new String[]{"External", "Internal"};
		FormData fd_comboDumpType = new FormData();
		fd_comboDumpType.left = new FormAttachment(0);
		fd_comboDumpType.top = new FormAttachment(0);
		fd_comboDumpType.right = new FormAttachment(FIRST_SEPARATOR_POSITION);
		comboDumpType.setLayoutData(fd_comboDumpType);
		comboDumpType.setItems(accociatedTypes);
		comboDumpType.select(stockpile.getType());
		
		final Composite stockpileNameComposite = new Composite(compositeRow, SWT.BORDER);
		FormData fd_stockpileNameComposite = new FormData();
		fd_stockpileNameComposite.left = new FormAttachment(FIRST_SEPARATOR_POSITION);
		fd_stockpileNameComposite.top = new FormAttachment(0);
		fd_stockpileNameComposite.right = new FormAttachment(SECOND_SEPARATOR_POSITION);
		fd_stockpileNameComposite.bottom = new FormAttachment(comboDumpType, 0, SWT.BOTTOM);
		stockpileNameComposite.setLayoutData(fd_stockpileNameComposite);
		stockpileNameComposite.setLayout(new FillLayout());
		
		createStockpileNameWidget(compositeRow,stockpileNameComposite, 0);
		
		comboDumpType.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				createStockpileNameWidget(compositeRow, stockpileNameComposite, comboDumpType.getSelectionIndex());
				stockpile.setType(comboDumpType.getSelectionIndex());
			}
		});
		
		final Combo comboPitGroup = new Combo(compositeRow, SWT.NONE);
		FormData fd_comboPitGroup = new FormData();
		fd_comboPitGroup.left = new FormAttachment(SECOND_SEPARATOR_POSITION);
		fd_comboPitGroup.top = new FormAttachment(0);
		fd_comboPitGroup.right = new FormAttachment(THIRD_SEPARATOR_POSITION);
		comboPitGroup.setLayoutData(fd_comboPitGroup);
		String[] pits = getPits();
		String[] pitGroups = getPitGroups();
		this.pitEndIndex = pits.length -1;
		this.pitGroupEndIndex = this.pitEndIndex + pitGroups.length;
		String[] comboItems = new String[pits.length + pitGroups.length];
		System.arraycopy(pits, 0, comboItems, 0, pits.length);
		System.arraycopy(pitGroups, 0, comboItems, pits.length, pitGroups.length);
		
		comboPitGroup.setItems(comboItems);

		comboPitGroup.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String text = comboPitGroup.getText();
				int index = comboPitGroup.getSelectionIndex();
				if(index <= pitEndIndex) {
					stockpile.setMappingType(0);
					stockpile.setAssociatedPit(projectConfiguration.getPitfromPitName(text));
				} else {
					stockpile.setMappingType(1);
					stockpile.setAssociatedPitGroup(projectConfiguration.getPitGroupfromName(text));
				}
			}
		});
		if(stockpile.getMappingType() == 0){
			if(stockpile.getAssociatedPit() != null) {
				comboPitGroup.setText(stockpile.getAssociatedPit().getPitName());
			}
		} else {
			if(stockpile.getAssociatedPitGroup() != null){
				comboPitGroup.setText(stockpile.getAssociatedPitGroup().getName());
			}
		}
		
		final Text textExpression = new Text(compositeRow, SWT.BORDER);
		FormData fd_textExpression = new FormData();
		fd_textExpression.left = new FormAttachment(THIRD_SEPARATOR_POSITION);
		fd_textExpression.top = new FormAttachment(0);
		fd_textExpression.right = new FormAttachment(FOURTH_SEPARATOR_POSITION);
		textExpression.setLayoutData(fd_textExpression);
		textExpression.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent event) {
				//TODO implement handler
				stockpile.setCondition(textExpression.getText());
			}
		});
		String condition = stockpile.getCondition();
		if(condition != null){
			textExpression.setText(condition);
		}
		
		final Button btnHasCapacity = new Button(compositeRow, SWT.CHECK);
		FormData fd_btnHasCapacity = new FormData();
		fd_btnHasCapacity.left = new FormAttachment(textExpression, 40, SWT.RIGHT);
		fd_btnHasCapacity.top = new FormAttachment(0, 2);
		btnHasCapacity.setLayoutData(fd_btnHasCapacity);
		btnHasCapacity.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				stockpile.setHasCapacity(btnHasCapacity.getSelection());
			}
		});
		btnHasCapacity.setSelection(stockpile.isHasCapacity());
		
		final Text textCapacity = new Text(compositeRow, SWT.BORDER);
		FormData fd_textCapacity = new FormData();
		fd_textCapacity.left = new FormAttachment(FIFTH_SEPARATOR_POSITION);
		fd_textCapacity.top = new FormAttachment(0);
		fd_textCapacity.right = new FormAttachment(SIXTH_SEPARATOR_POSITION);
		textCapacity.setLayoutData(fd_textCapacity);
		textCapacity.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent event) {
				stockpile.setCapacity(Integer.parseInt(textCapacity.getText()));
			}
		});
		String capacity = String.valueOf(stockpile.getCapacity());
		if((capacity!=null) && !(capacity.equals(""))){
			textCapacity.setText(capacity);
		}
		
		final Button btnIsReclaim = new Button(compositeRow, SWT.CHECK);
		FormData fd_btnIsReclaim = new FormData();
		fd_btnIsReclaim.left = new FormAttachment(textCapacity, 40, SWT.RIGHT);
		fd_btnIsReclaim.top = new FormAttachment(0, 2);
		btnIsReclaim.setLayoutData(fd_btnIsReclaim);
		btnIsReclaim.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				stockpile.setReclaim(btnIsReclaim.getSelection());
			}
		});
		btnIsReclaim.setSelection(stockpile.isReclaim());
		
		this.presentRow = compositeRow;
		compositeRow.setLayoutData(fd_compositeRow);
		this.layout();
		
		
	}

	public void addRow(){
		Stockpile stockpile = new Stockpile();
		this.stockpileList.add(stockpile);
		this.addRow(stockpile);
	}

	public List<Composite> getAllRowsComposite(){
		return this.allRows;
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
