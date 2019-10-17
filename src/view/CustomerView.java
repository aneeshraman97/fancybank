package view;

import java.awt.EventQueue;
import java.util.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.border.Border;

import model.*;

import java.awt.Font;

import javax.swing.*;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.Insets;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import controller.Bank;
import javafx.collections.SetChangeListener;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class CustomerView extends JFrame implements Observer {
	private BankCustomer customer;
	private Bank bank;

	private JList<String> accountsList;
	private JList<String> transactionsList;
	private JLabel lblCustomerView;
	private DefaultListModel<String> accountsModel;
	private DefaultListModel<String> transactionsModel;
	private JLabel accountNameLbl;
	private JLabel accountBalanceLbl;

	/**
	 * Create the application.
	 */
	public CustomerView(Bank bank, BankCustomer customer) {

		this.bank = bank;
		this.customer = this.bank.getCustomerByEmail(customer.getEmail());
		this.bank.addObserver(this);
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		this.setBounds(100, 100, 1600, 800);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout(0, 0));

		lblCustomerView = new JLabel("Customer View ");
		lblCustomerView.setFont(new Font("Tahoma", Font.PLAIN, 45));
		lblCustomerView.setHorizontalAlignment(SwingConstants.CENTER);
		getContentPane().add(lblCustomerView, BorderLayout.NORTH);

		JPanel westPanel = new JPanel();
		getContentPane().add(westPanel, BorderLayout.WEST);
		westPanel.setLayout(new BorderLayout(0, 0));
		westPanel.setPreferredSize(new Dimension(400, 800));

		JPanel accountsPanel = new JPanel();
		accountsPanel.setPreferredSize(new Dimension(400, 300));
		westPanel.add(accountsPanel, BorderLayout.NORTH);

		accountsModel = new DefaultListModel<>();
		accountsModel = addAccountsToList(this.customer, accountsModel);
		accountsList = new JList<String>(accountsModel);
		accountsList.setValueIsAdjusting(true);
		accountsList.addListSelectionListener(new AccountListListener());
		accountsPanel.setLayout(new BorderLayout(0, 0));

		accountsList.setVisibleRowCount(5);
		accountsList.setPreferredSize(new Dimension(380, 280));
		accountsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		accountsList.setBorder(new EmptyBorder(5, 5, 5, 5));
		accountsList.setFont(new Font("Tahoma", Font.PLAIN, 30));
		accountsPanel.add(accountsList);

		JPanel centerPanel = new JPanel();
		getContentPane().add(centerPanel, BorderLayout.CENTER);
		centerPanel.setLayout(new BorderLayout(0, 0));

		JPanel btnsPanel = new JPanel();
		centerPanel.add(btnsPanel, BorderLayout.CENTER);
		btnsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JButton btnWithdraw = new JButton("Withdraw");
		btnWithdraw.setFont(new Font("Tahoma", Font.PLAIN, 30));
		btnWithdraw.addActionListener(new TransactionActionListener("Withdraw From Account", "Withdraw"));
		btnsPanel.add(btnWithdraw);

		JButton btnDeposit = new JButton("Deposit");
		btnDeposit.addActionListener(new TransactionActionListener("Deposit into Account", "Deposit"));
		btnDeposit.setFont(new Font("Tahoma", Font.PLAIN, 30));
		btnsPanel.add(btnDeposit);

		JButton btnTransfer = new JButton("Transfer");
		btnTransfer.addActionListener(new TransferBtnListener());
		btnTransfer.setFont(new Font("Tahoma", Font.PLAIN, 30));
		btnsPanel.add(btnTransfer);

		JButton btnAddAccount = new JButton("Add Account");
		btnAddAccount.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				JTextField nameField = new JTextField();

				UIManager.put("OptionPane.minimumSize", new Dimension(600, 300));
				UIManager.put("ComboBox.font", new Font("Tahoma", Font.PLAIN, 30));
				JComboBox<String> combo = new JComboBox<String>();
				ArrayList<String> accountType = new ArrayList<String>();
				accountType.add("Savings");
				accountType.add("Checking");
			
				for (int i = 0; i < accountType.size(); i++) {
					combo.addItem(accountType.get(i));
				}

				Object[] fields = { "Account name", nameField, "Account Type: ", combo };

				int reply = JOptionPane.showConfirmDialog(null, fields, "Choose Account Type",
						JOptionPane.OK_CANCEL_OPTION);

				if (reply == JOptionPane.OK_OPTION) {
					String type; 
					type = combo.getItemAt(combo.getSelectedIndex());

					bank.addAccount(customer, nameField.getText(), type);
				}
			}
		});
		btnAddAccount.setFont(new Font("Tahoma", Font.PLAIN, 30));
		btnsPanel.add(btnAddAccount);

		JLabel messageLabel = new JLabel("Message");
		messageLabel.setFont(new Font("Tahoma", Font.PLAIN, 30));
		messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
		centerPanel.add(messageLabel, BorderLayout.SOUTH);

		JPanel eastPanel = new JPanel();
		eastPanel.setPreferredSize(new Dimension(500, 800));
		getContentPane().add(eastPanel, BorderLayout.EAST);
		GridBagLayout gbl_eastPanel = new GridBagLayout();
		gbl_eastPanel.columnWidths = new int[] { 376, 0 };
		gbl_eastPanel.rowHeights = new int[] { 265, 265, 0 };
		gbl_eastPanel.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
		gbl_eastPanel.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		eastPanel.setLayout(gbl_eastPanel);

		JPanel balancePanel = new JPanel();
		balancePanel.setPreferredSize(new Dimension(500, 150));
		GridBagConstraints gbc_balancePanel = new GridBagConstraints();
		gbc_balancePanel.fill = GridBagConstraints.BOTH;
		gbc_balancePanel.insets = new Insets(0, 0, 5, 0);
		gbc_balancePanel.gridx = 0;
		gbc_balancePanel.gridy = 0;
		balancePanel.setLayout(new BorderLayout(0, 0));
		eastPanel.add(balancePanel, gbc_balancePanel);

		JLabel lblNewLabel = new JLabel("Balance");
		lblNewLabel.setVerticalAlignment(SwingConstants.BOTTOM);
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 34));
		balancePanel.add(lblNewLabel, BorderLayout.NORTH);

		Border border = BorderFactory.createEmptyBorder(5, 5, 5, 5);

		JPanel balanceDisplayPanel = new JPanel();
		balanceDisplayPanel.setPreferredSize(new Dimension(500, 100));
		balancePanel.add(balanceDisplayPanel);
		balanceDisplayPanel.setLayout(new GridLayout(0, 2, 0, 0));
		accountNameLbl = new JLabel("Account name: ");
		accountNameLbl.setHorizontalAlignment(SwingConstants.RIGHT);
		accountNameLbl.setPreferredSize(new Dimension(230, 50));
		accountNameLbl.setBorder(border);
		balanceDisplayPanel.add(accountNameLbl);

		accountBalanceLbl = new JLabel("$ 0.0");
		accountBalanceLbl.setPreferredSize(new Dimension(230, 50));
		accountBalanceLbl.setBorder(border);
		balanceDisplayPanel.add(accountBalanceLbl);

		JPanel loansPanel = new JPanel();
		GridBagConstraints gbc_loansPanel = new GridBagConstraints();
		gbc_loansPanel.fill = GridBagConstraints.BOTH;
		gbc_loansPanel.gridx = 0;
		gbc_loansPanel.gridy = 1;
		eastPanel.add(loansPanel, gbc_loansPanel);
		loansPanel.setLayout(new BorderLayout(0, 0));

		JLabel lblNewLabel_1 = new JLabel("Loans");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lblNewLabel_1.setVerticalAlignment(SwingConstants.BOTTOM);
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		loansPanel.add(lblNewLabel_1, BorderLayout.NORTH);

		JPanel loansDisplayPanel = new JPanel();
		loansPanel.add(loansDisplayPanel, BorderLayout.CENTER);

		accountsPanel.setBorder(new LineBorder(Color.black, 3));

		JLabel lblAccounts = new JLabel("Accounts");
		accountsPanel.add(lblAccounts, BorderLayout.NORTH);
		lblAccounts.setHorizontalAlignment(SwingConstants.CENTER);
		lblAccounts.setFont(new Font("Tahoma", Font.PLAIN, 34));

		JPanel transactionsPanel = new JPanel();
		westPanel.add(transactionsPanel, BorderLayout.CENTER);
		transactionsPanel.setLayout(new BorderLayout(0, 0));

		JLabel lblTransactions = new JLabel("Transactions");
		lblTransactions.setHorizontalAlignment(SwingConstants.CENTER);
		lblTransactions.setFont(new Font("Tahoma", Font.PLAIN, 34));
		transactionsPanel.add(lblTransactions, BorderLayout.NORTH);

		accountsModel = new DefaultListModel<>();
		// change to transactions here
		accountsModel = addAccountsToList(this.customer, accountsModel);
		transactionsList = new JList<String>(accountsModel);
		transactionsList.addListSelectionListener(new AccountListListener());
		transactionsList.setVisible(true);

		transactionsList.setVisibleRowCount(8);
		transactionsList.setPreferredSize(new Dimension(380, 380));
		transactionsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		transactionsList.setBorder(new EmptyBorder(5, 5, 5, 5));
		transactionsList.setFont(new Font("Tahoma", Font.PLAIN, 30));
		transactionsPanel.add(transactionsList);
		// transactionsPanel.setPreferredSize(new Dimension(400, 400));

		btnsPanel.setBorder(new LineBorder(Color.black, 3));
		balancePanel.setBorder(new LineBorder(Color.black, 3));
		transactionsPanel.setBorder(new LineBorder(Color.black, 3));
	}

	public class TransactionActionListener implements ActionListener {

		String title;
		String type;

		public TransactionActionListener(String title, String type) {
			// TODO Auto-generated constructor stub
			this.title = title;
			this.type = type;
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			if (customer.getAccounts().size() == 0) {
				JOptionPane.showMessageDialog(null, "No Accounts exist for Customer", "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			UIManager.put("OptionPane.minimumSize", new Dimension(600, 300));
			UIManager.put("ComboBox.font", new Font("Tahoma", Font.PLAIN, 30));

			JComboBox<String> combo = new JComboBox<String>();
			ArrayList<BankAccount> accounts = customer.getAccounts();
			for (int i = 0; i < accounts.size(); i++) {
				combo.addItem(accounts.get(i).getAccountName());
			}
			JTextField amountField = new JTextField();

			Object[] fields = { "Account Name: ", combo, "Amount in USD: $", amountField, };

			int reply = JOptionPane.showConfirmDialog(null, fields, title, JOptionPane.OK_CANCEL_OPTION);

			if (reply == JOptionPane.OK_OPTION) {
				int accountIndex = combo.getSelectedIndex();
				if (this.type.equals("Deposit")) {
					bank.depositForCustomer(customer, combo.getItemAt(accountIndex),
							Double.parseDouble(amountField.getText()));
				} else if (this.type.equals("Withdraw")) {
					bank.withdrawForCustomer(customer, combo.getItemAt(accountIndex),
							Double.parseDouble(amountField.getText()));
				}
			}
		}

	}

	public class TransferBtnListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			if (customer.getAccounts().size() == 0) {
				JOptionPane.showMessageDialog(null, "No Accounts exist for Customer", "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			while (true) {
				UIManager.put("OptionPane.minimumSize", new Dimension(600, 300));
				UIManager.put("ComboBox.font", new Font("Tahoma", Font.PLAIN, 30));

				JComboBox<String> combo1 = new JComboBox<String>();
				JComboBox<String> combo2 = new JComboBox<String>();
				ArrayList<BankAccount> accounts = customer.getAccounts();
				for (int i = 0; i < accounts.size(); i++) {
					combo1.addItem(accounts.get(i).getAccountName());
					combo2.addItem(accounts.get(i).getAccountName());
				}

				JTextField amountField = new JTextField();

				Object[] fields = { "From Account Name: ", combo1, "To Account Name: ", combo2, "Amount in USD: $",
						amountField, };

				int reply = JOptionPane.showConfirmDialog(null, fields, "Transfer between Accounts",
						JOptionPane.OK_CANCEL_OPTION);
				if (reply == JOptionPane.OK_OPTION) {
					int index1 = combo1.getSelectedIndex();
					int index2 = combo2.getSelectedIndex();
					String amountString = amountField.getText();
//					if(!amountString.contains("."))
//						amountString.concat(".0");
					double amount = Double.parseDouble(amountString);
					if (index1 != index2) {
						if (amount > 0) {
							bank.transferBetweenAccountsForCustomer(customer, accounts.get(index1).getAccountName(),
									accounts.get(index2).getAccountName(), amount);
							break;
						} else {
							JOptionPane.showMessageDialog(null, "Amount cannot be less than or equal to 0", "Error",
									JOptionPane.ERROR_MESSAGE);
						}
					}
					JOptionPane.showMessageDialog(null, "From and to accounts cannot be the same!", "Error",
							JOptionPane.ERROR_MESSAGE);
				}

			}

		}

	}

	public class AccountListListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent arg0) {
			// TODO Auto-generated method stub

			ArrayList<BankAccount> accounts = customer.getAccounts();
			BankAccount account = accounts.get(accountsList.getSelectedIndex());

			accountNameLbl.setText(account.getAccountName());
			Double balance = account.getBalance();
			accountBalanceLbl.setText(balance.toString());
		}

	}

	public DefaultListModel<String> addAccountsToList(BankCustomer customer, DefaultListModel<String> model) {
		customer = this.bank.getCustomerByEmail(customer.getEmail());
		ArrayList<BankAccount> accounts = customer.getAccounts();
		for (BankAccount acc : accounts) {
			model.addElement(acc.getAccountName() + "\t" + acc.getType());
		}
		return model;
	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub

		accountsModel = addAccountsToList(this.customer, new DefaultListModel<String>());
		accountsList.setModel(accountsModel);
	}

}
