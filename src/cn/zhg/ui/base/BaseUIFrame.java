/**
 * 
 */
package cn.zhg.ui.base;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Label;
import java.awt.RenderingHints;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.metal.MetalIconFactory;

import cn.zhg.ui.base.BaseUIFrame.PTextField;

/**
 * @author zhg
 *
 *         创建于 2016年3月24日 上午12:53:39
 */
@SuppressWarnings("serial")
public class BaseUIFrame extends JFrame
{
	private static java.util.logging.Logger log = java.util.logging.Logger.getLogger(BaseUIFrame.class.getName());

	private JDialog dia;

	public void showProcess()
	{
		showProcess("正在运行");
	}

	public void hideProcess()
	{
		if (dia != null)
		{
			dia.setVisible(false);
		}
	}

	/**
	 * 
	 */
	public void showProcess(String msg)
	{
		if (dia == null)
		{
			dia = new JDialog(this);
			dia.setTitle("进度");
			dia.setIconImage(this.getIconImage());
			dia.setLayout(new FlowLayout());
			JProgressBar bar = new JProgressBar();
			bar.setStringPainted(true);
			bar.setIndeterminate(true);
			dia.setSize(100, 80);
			dia.setResizable(false);
			dia.add(bar);
		}
		((JProgressBar) dia.getContentPane().getComponent(0)).setString(msg);
		dia.setLocationRelativeTo(this);
		dia.setVisible(true);
	}

	public void alert(String msg)
	{
		JOptionPane.showMessageDialog(null, msg);
	}

	public void alert(String title, String msg)
	{
		JOptionPane.showMessageDialog(null, msg, title, JOptionPane.INFORMATION_MESSAGE);
	}

	public void warn(String msg)
	{
		ExpandDialog dia = new ExpandDialog(this, JOptionPane.WARNING_MESSAGE);
		dia.setMessage("程序正常运行,但出现问题如下:");
		if (msg != null)
		{
			dia.setExpandContent(msg);
		}
		dia.setVisible(true);
	}

	public void error(String msg, Throwable ex)
	{
		ExpandDialog dia = new ExpandDialog(this, JOptionPane.ERROR_MESSAGE);
		dia.setMessage(msg);
		if (ex != null)
		{
			StringWriter sw = new StringWriter();
			ex.printStackTrace(new PrintWriter(sw));
			dia.setExpandContent(sw.toString());
		}
		dia.setVisible(true);
	}

	public void error(String msg)
	{
		ExpandDialog dia = new ExpandDialog(this, JOptionPane.ERROR_MESSAGE);
		dia.setMessage("程序不能正常运行,错误如下:");
		if (msg != null)
		{
			dia.setExpandContent(msg);
		}
		dia.setVisible(true);
	}

	/**
	 * 保存Component状态
	 * 
	 * @param fields
	 */
	public void saveComponent(Component... fields)
	{
		if (fields != null)
		{
			for (Component f : fields)
			{
				String name = f.getName();
				String value = UISetting.getString(name);
				if (f instanceof TextField)
				{
					TextField _f = (TextField) f;
					_f.setText(value);
					_f.addTextListener(l -> {
						UISetting.put(name, _f.getText());
					});

				} else if (f instanceof JTextField)
				{
					JTextField _f = (JTextField) f;
					_f.setText(value);
					_f.getDocument().addDocumentListener(new DocumentListener()
					{

						@Override
						public void insertUpdate(DocumentEvent e)
						{
							UISetting.put(name, _f.getText());
						}

						@Override
						public void removeUpdate(DocumentEvent e)
						{
							UISetting.put(name, _f.getText());

						}

						@Override
						public void changedUpdate(DocumentEvent e)
						{

						}
					});
				} else if (f instanceof FilePicker)
				{
					FilePicker _f = (FilePicker) f;
					_f.setText(value);
					_f.addTextListener(l -> {
						UISetting.put(name, _f.getText());
					});
				}
			}
		}
	}

	/**
	 * @param label
	 * @return
	 */
	public JButton addButton(Container cnt, String label)
	{
		JButton field = null;
		JPanel panel = new JPanel(new FlowLayout());
		field = new JButton(label);
		panel.add(field);
		panel.setMaximumSize(new Dimension(this.getMaximumSize().width, 50));
		cnt.add(panel);
		return field;
	}

	/**
	 * @param string
	 * @return
	 */
	public TextField addOpenChoise(Container cnt, String label)
	{
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(new Label(label), BorderLayout.WEST);
		TextField field = new TextField();
		panel.add(field, BorderLayout.CENTER);
		panel.setMaximumSize(new Dimension(this.getMaximumSize().width, 50));
		Button btn = new Button("选择");
		panel.add(btn, BorderLayout.EAST);
		cnt.add(panel);
		btn.addActionListener(l -> {
			if (field.isEnabled())
			{
				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle(label);
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

				chooser.showOpenDialog(null);
				chooser.setDialogType(JFileChooser.CUSTOM_DIALOG);
				File file = chooser.getSelectedFile();
				if (file != null)
				{
					try
					{
						field.setText(file.getCanonicalPath());
					} catch (Exception e)
					{
						field.setText(file.getPath());
					}
				}
			}
		});
		DropTargetListener dtl = new DropTargetListener()
		{

			@Override
			public void dragEnter(DropTargetDragEvent dtde)
			{
				// System.out.println("dragEnter"+dtde);
			}

			@Override
			public void dragOver(DropTargetDragEvent dtde)
			{
				// System.out.println("dragOver"+dtde);

			}

			@Override
			public void dropActionChanged(DropTargetDragEvent dtde)
			{
				// System.out.println("dropActionChanged"+dtde);

			}

			@Override
			public void dragExit(DropTargetEvent dtde)
			{
				// System.out.println("dragExit"+dtde);

			}

			@SuppressWarnings("unchecked")
			@Override
			public void drop(DropTargetDropEvent dtde)
			{
				Transferable tr = dtde.getTransferable();
				if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
				{
					dtde.acceptDrop(dtde.getDropAction());
					try
					{
						java.util.List<File> s = (java.util.List<File>) tr
								.getTransferData(DataFlavor.javaFileListFlavor);
						// log.log(Level.INFO, "拖入文件 :" + s);
						if (!s.isEmpty())
						{
							File file = s.get(0);
							try
							{
								field.setText(file.getCanonicalPath());
							} catch (Exception e)
							{
								field.setText(file.getPath());
							}
						}
						dtde.dropComplete(true);
					} catch (UnsupportedFlavorException | IOException e)
					{
						e.printStackTrace();
						log.log(Level.INFO, "拖入文件错误", e);
					}
				} else
				{
					dtde.rejectDrop();
					log.log(Level.INFO, "不可用拖动内容");
				}
			}
		};
		field.setDropTarget(new DropTarget(field, DnDConstants.ACTION_MOVE, dtl));
		return field;
	}

	public TextField addSaveChoise(Container cnt, String label)
	{
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(new Label(label), BorderLayout.WEST);
		TextField field = new TextField();
		panel.add(field, BorderLayout.CENTER);
		panel.setMaximumSize(new Dimension(this.getMaximumSize().width, 50));
		Button btn = new Button("选择");
		panel.add(btn, BorderLayout.EAST);
		cnt.add(panel);
		btn.addActionListener(l -> {
			if (field.isEnabled())
			{
				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle(label);
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				chooser.showSaveDialog(null);
				chooser.setDialogType(JFileChooser.CUSTOM_DIALOG);
				File file = chooser.getSelectedFile();
				if (file != null)
				{
					try
					{
						field.setText(file.getCanonicalPath());
					} catch (Exception e)
					{
						field.setText(file.getPath());
					}
				}
			}
		});
		DropTargetListener dtl = new DropTargetListener()
		{

			@Override
			public void dragEnter(DropTargetDragEvent dtde)
			{
				// System.out.println("dragEnter"+dtde);
			}

			@Override
			public void dragOver(DropTargetDragEvent dtde)
			{
				// System.out.println("dragOver"+dtde);

			}

			@Override
			public void dropActionChanged(DropTargetDragEvent dtde)
			{
				// System.out.println("dropActionChanged"+dtde);

			}

			@Override
			public void dragExit(DropTargetEvent dtde)
			{
				// System.out.println("dragExit"+dtde);

			}

			@SuppressWarnings("unchecked")
			@Override
			public void drop(DropTargetDropEvent dtde)
			{
				Transferable tr = dtde.getTransferable();
				if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
				{
					dtde.acceptDrop(dtde.getDropAction());
					try
					{
						java.util.List<File> s = (java.util.List<File>) tr
								.getTransferData(DataFlavor.javaFileListFlavor);
						// log.log(Level.INFO, "拖入文件 :" + s);
						if (!s.isEmpty())
						{
							File file = s.get(0);
							try
							{
								field.setText(file.getCanonicalPath());
							} catch (Exception e)
							{
								field.setText(file.getPath());
							}
						}
						dtde.dropComplete(true);
					} catch (UnsupportedFlavorException | IOException e)
					{
						e.printStackTrace();
						log.log(Level.INFO, "拖入文件错误", e);
					}
				} else
				{
					dtde.rejectDrop();
					log.log(Level.INFO, "不可用拖动内容");
				}
			}
		};
		field.setDropTarget(new DropTarget(field, DnDConstants.ACTION_MOVE, dtl));
		return field;
	}

	@SuppressWarnings("unchecked")
	public TextField addSaveDirChoise(Container cnt, String label)
	{
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(new Label(label), BorderLayout.WEST);
		TextField field = new TextField();
		panel.add(field, BorderLayout.CENTER);
		panel.setMaximumSize(new Dimension(this.getMaximumSize().width, 50));
		Button btn = new Button("选择");
		panel.add(btn, BorderLayout.EAST);
		cnt.add(panel);
		btn.addActionListener(l -> {
			if (field.isEnabled())
			{
				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle(label);
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.showSaveDialog(null);
				chooser.setDialogType(JFileChooser.CUSTOM_DIALOG);
				File file = chooser.getSelectedFile();
				if (file != null)
				{
					try
					{
						field.setText(file.getCanonicalPath());
					} catch (Exception e)
					{
						field.setText(file.getPath());
					}
				}
			}
		});
		DropTargetListener dtl = new DropTargetListener()
		{

			@Override
			public void dragEnter(DropTargetDragEvent dtde)
			{
				// System.out.println("dragEnter"+dtde);
			}

			@Override
			public void dragOver(DropTargetDragEvent dtde)
			{
				// System.out.println("dragOver"+dtde);

			}

			@Override
			public void dropActionChanged(DropTargetDragEvent dtde)
			{
				// System.out.println("dropActionChanged"+dtde);

			}

			@Override
			public void dragExit(DropTargetEvent dtde)
			{
				// System.out.println("dragExit"+dtde);

			}

			@Override
			public void drop(DropTargetDropEvent dtde)
			{
				Transferable tr = dtde.getTransferable();
				if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
				{
					dtde.acceptDrop(dtde.getDropAction());
					try
					{
						java.util.List<File> s = (java.util.List<File>) tr
								.getTransferData(DataFlavor.javaFileListFlavor);
						// log.log(Level.INFO, "拖入文件 :" + s);
						if (!s.isEmpty())
						{
							File file = s.get(0);
							try
							{
								field.setText(file.getCanonicalPath());
							} catch (Exception e)
							{
								field.setText(file.getPath());
							}
						}
						dtde.dropComplete(true);
					} catch (UnsupportedFlavorException | IOException e)
					{
						e.printStackTrace();
						log.log(Level.INFO, "拖入文件错误", e);
					}
				} else
				{
					dtde.rejectDrop();
					log.log(Level.INFO, "不可用拖动内容");
				}
			}
		};
		field.setDropTarget(new DropTarget(field, DnDConstants.ACTION_MOVE, dtl));
		return field;
	}

	@SuppressWarnings("unchecked")
	public TextField addFileChoise(Container cnt, String label)
	{
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(new Label(label), BorderLayout.WEST);
		TextField field = new TextField();
		panel.add(field, BorderLayout.CENTER);
		panel.setMaximumSize(new Dimension(this.getMaximumSize().width, 50));
		Button btn = new Button("选择");
		panel.add(btn, BorderLayout.EAST);
		cnt.add(panel);
		btn.addActionListener(l -> {
			if (field.isEnabled())
			{
				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle(label);
				chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				chooser.showDialog(null, "确认");
				chooser.setDialogType(JFileChooser.CUSTOM_DIALOG);
				File file = chooser.getSelectedFile();
				if (file != null)
				{
					try
					{
						field.setText(file.getCanonicalPath());
					} catch (Exception e)
					{
						field.setText(file.getPath());
					}
				}
			}
		});
		DropTargetListener dtl = new DropTargetListener()
		{

			@Override
			public void dragEnter(DropTargetDragEvent dtde)
			{
				// System.out.println("dragEnter"+dtde);
			}

			@Override
			public void dragOver(DropTargetDragEvent dtde)
			{
				// System.out.println("dragOver"+dtde);

			}

			@Override
			public void dropActionChanged(DropTargetDragEvent dtde)
			{
				// System.out.println("dropActionChanged"+dtde);

			}

			@Override
			public void dragExit(DropTargetEvent dtde)
			{
				// System.out.println("dragExit"+dtde);

			}

			@Override
			public void drop(DropTargetDropEvent dtde)
			{
				Transferable tr = dtde.getTransferable();
				if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
				{
					dtde.acceptDrop(dtde.getDropAction());
					try
					{
						java.util.List<File> s = (java.util.List<File>) tr
								.getTransferData(DataFlavor.javaFileListFlavor);
						// log.log(Level.INFO, "拖入文件 :" + s);
						if (!s.isEmpty())
						{
							File file = s.get(0);
							try
							{
								field.setText(file.getCanonicalPath());
							} catch (Exception e)
							{
								field.setText(file.getPath());
							}
						}
						dtde.dropComplete(true);
					} catch (UnsupportedFlavorException | IOException e)
					{
						e.printStackTrace();
						log.log(Level.INFO, "拖入文件错误", e);
					}
				} else
				{
					dtde.rejectDrop();
					log.log(Level.INFO, "不可用拖动内容");
				}
			}
		};
		field.setDropTarget(new DropTarget(field, DnDConstants.ACTION_MOVE, dtl));
		return field;
	}

	public PTextField addLabelField(Container cnt, String label)
	{
		return addLabelField(cnt, label, null);
	}

	public PTextField addLabelField(Container cnt, String label, String tip)
	{
		TextFieldPanel panel = new TextFieldPanel(label,tip); 
		cnt.add(panel);
		return panel.getTextField();
	}

	public static Image loadImage(String src)
	{
		// return new
		// ImageIcon(this.getClass().getResource(src).toString()).getImage();

		// try
		// {
		// return ImageIO.read(this.getClass().getResource(src));
		// } catch (IOException e)
		// {
		// e.printStackTrace();
		// }
		URL res = Object.class.getClass().getResource(src);
		if (res == null)
		{
			return null;
		} else
		{
			return Toolkit.getDefaultToolkit().getImage(res);
		}
	}

	private static int uiIndex = 0;

	public static int getUIIndex()
	{
		return uiIndex;
	}

	public static void switchUI()
	{
		List<LookAndFeelInfo> feels = Arrays.asList(UIManager.getInstalledLookAndFeels());
		log.log(Level.INFO, "共有" + feels.size() + "套皮肤，当前" + uiIndex + ",名称:" + feels.get(uiIndex).getClassName());
		try
		{
			UIManager.setLookAndFeel(feels.get(uiIndex).getClassName());
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		uiIndex = ++uiIndex % feels.size();
	}
	public static class TextFieldPanel extends Box
	{

		/** */
		private static final long serialVersionUID = 1L;
		private PTextField inputField;

		public TextFieldPanel(String label)
		{
			this(label, null);
		}

		public TextFieldPanel(String label, String tip)
		{
			super(BoxLayout.X_AXIS);
			add(new JLabel(label));
			inputField = new PTextField();
			inputField.setToolTipText(tip);
			add(inputField);
			add(Box.createHorizontalGlue());
			setMaximumSize(new Dimension(getMaximumSize().width, 30));
		}
		/**
		 * @return
		 */
		public PTextField getTextField()
		{ 
			return inputField;
		}
		/**
		 * @return
		 */
		public String getString()
		{ 
			return getString(null);
		}
		/**
		 * @return
		 */
		public String getString(String def)
		{
			String str = inputField.getText();
			if ("".equals(str))
			{
				return def;
			}
			return str;
		}
		/**
		 * @return
		 */
		public Integer getInteger()
		{
			String str = inputField.getText();
			if ("".equals(str))
			{
				return null;
			}
			try
			{
				return Integer.parseInt(str);
			} catch (Exception igr)
			{

			}
			return null;
		}

		/**
		 * @return
		 */
		public Double getDouble()
		{
			String str = inputField.getText();
			if ("".equals(str))
			{
				return null;
			}
			try
			{
				return Double.parseDouble(str);
			} catch (Exception igr)
			{

			}
			return null;
		}

	}
	public static class FileSavePicker extends FilePicker
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -279946874966243505L;

		public	FileSavePicker(String lName)
		{
			super(true, lName, null);
		}
		public	FileSavePicker(String lName, String tip)
		{
			super(false, lName, tip,  (String[])null);
		}
		public	FileSavePicker(String lName, String tip, String... exts)
		{
			super(false, lName, tip, exts);
		}
		public	FileSavePicker(String lName, String tip, FileFilter... exts)
		{
			super(false, false,false, lName,"选择",tip,exts);
		}
	}
	public static class DirectoryOpenPicker extends FilePicker
	{
		public	DirectoryOpenPicker(String lName)
		{
			this(  lName, null);
		}
		public	DirectoryOpenPicker(String lName, String tip)
		{
			super(true, false, lName, "选择", tip, (String[])null); 
		}
	}
	public static class FileOpenPicker extends FilePicker
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -8242182068979674348L;

		public	FileOpenPicker(String lName)
		{
			super(true, lName, null);
		}
		public	FileOpenPicker(String lName, String tip)
		{
			super(true, lName, tip, (String[])null);
		}
		public	FileOpenPicker(String lName, String tip, String... exts)
		{
			super(true, lName, tip, exts);
		}
		public	FileOpenPicker(String lName, String tip, FileFilter... exts)
		{
			super(true, false,false, lName,"选择",tip,exts);
		}
	}

	@SuppressWarnings("unchecked")
	public static class FilePicker extends JPanel
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 5195562611603761860L;
		private JLabel label;
		private PTextField text;
		private JButton btn;
		private List<File> fs;
		private List<TextListener> tls;
		private String toolTip;

		public	FilePicker(boolean isOpen, String lName, String tip, String... exts)
		{
			this(isOpen, isOpen, lName, "选择", tip, exts);
		}
	 
		/**
		 * @param object
		 */
		public void addTextListener(TextListener tl)
		{
			this.tls.add(tl);
		}

		public	FilePicker(boolean isOpen, boolean onlyFile, String lName, String bName, String tip, String... exts)
		{
			super(new BorderLayout());
			if (exts != null && exts.length > 0)
			{
				init(isOpen, onlyFile, false, lName, tip, bName, new FileNameExtensionFilter("可用文件", exts));
			} else
			{
				init(isOpen, onlyFile, false, lName, tip, bName);
			}
		}

		public	FilePicker(boolean isOpen, boolean onlyFile, String lName, String bName, String tip, FileFilter... filters)
		{
			super(new BorderLayout());
			init(isOpen, onlyFile, false, lName, tip, bName, filters);
		}
		public	FilePicker(boolean isOpen, boolean onlyFile, boolean isMulti, String lName, String bName, String tip, FileFilter... filters)
		{
			super(new BorderLayout());
			init(isOpen, onlyFile, isMulti, lName, tip, bName, filters);
		}
		/**
		 * @param isOpen
		 * @param lName
		 * @param tip
		 * @param bName
		 * @param filters
		 */
		private void init(boolean isOpen, boolean onlyFile, boolean isMulti, String lName, String tip, String bName,
				FileFilter... filters)
		{
			fs = new ArrayList<>();
			tls = new ArrayList<>();
			label = new JLabel(lName);
			this.setName(lName);
			add(label, BorderLayout.WEST);
			text = new PTextField();
			add(text, BorderLayout.CENTER);
			setMaximumSize(new Dimension(this.getMaximumSize().width, 50));
			btn = new JButton(bName);
			add(btn, BorderLayout.EAST);
			if (tip != null)
			{
				text.setToolTipText(tip);
			}
			toolTip=tip;
			btn.addActionListener(l -> {
				if (text.isEnabled())
				{
					JFileChooser chooser = new JFileChooser();
					chooser.setMultiSelectionEnabled(isMulti);
					if(filters!=null)
					{
						for(FileFilter filter:filters)
						{
							chooser.addChoosableFileFilter(filter);
						} 
					}
					
					if (tip != null)
					{
						chooser.setDialogTitle(tip);
					}
					if (onlyFile)
					{
						chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
					} else
					{
						chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
					}
					chooser.setDialogType(JFileChooser.CUSTOM_DIALOG);
					int ret;
					if (isOpen)
					{
						ret=	chooser.showOpenDialog(null);
					} else
					{
						ret=	chooser.showSaveDialog(null);
					} 
					if(ret!=JFileChooser.APPROVE_OPTION)
					{
						return;
					}
					if (isMulti)
					{
						File[] _fs = chooser.getSelectedFiles();
						if (_fs != null)
						{
							setFile(_fs);
						}
					} else
					{
						File _fs = chooser.getSelectedFile();
						if (_fs != null)
						{
							setFile(_fs);
						}
					}
				}
			});
			DropTargetListener dtl = new DropTargetListener()
			{

				@Override
				public void dragEnter(DropTargetDragEvent dtde)
				{
					// System.out.println("dragEnter"+dtde);
				}

				@Override
				public void dragOver(DropTargetDragEvent dtde)
				{
					// System.out.println("dragOver"+dtde);

				}

				@Override
				public void dropActionChanged(DropTargetDragEvent dtde)
				{
					// System.out.println("dropActionChanged"+dtde);

				}

				@Override
				public void dragExit(DropTargetEvent dtde)
				{
					// System.out.println("dragExit"+dtde);

				}

				@Override
				public void drop(DropTargetDropEvent dtde)
				{
					Transferable tr = dtde.getTransferable();
					if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
					{
						dtde.acceptDrop(dtde.getDropAction());
						try
						{
							java.util.List<File> _fs = (java.util.List<File>) tr
									.getTransferData(DataFlavor.javaFileListFlavor);
							// log.log(Level.INFO, "拖入文件 :" + s);
							if (!_fs.isEmpty())
							{
								setFile(_fs);
							}
							dtde.dropComplete(true);
						} catch (UnsupportedFlavorException | IOException e)
						{
							e.printStackTrace();
							log.log(Level.INFO, "拖入文件错误", e);
						}
					} else
					{
						dtde.rejectDrop();
						log.log(Level.INFO, "不可用拖动内容");
					}
				}
			};
			text.setDropTarget(new DropTarget(text, DnDConstants.ACTION_MOVE, dtl));
			text.getDocument().addDocumentListener(new DocumentListener()
			{

				@Override
				public void insertUpdate(DocumentEvent e)
				{
					if (tls != null && !tls.isEmpty())
					{
						for (TextListener tl : tls)
						{
							tl.textValueChanged(new TextEvent(text, TextEvent.TEXT_VALUE_CHANGED));
						}
					}
				}

				@Override
				public void removeUpdate(DocumentEvent e)
				{
					if (tls != null && !tls.isEmpty())
					{
						for (TextListener tl : tls)
						{
							tl.textValueChanged(new TextEvent(text, TextEvent.TEXT_VALUE_CHANGED));
						}
					}
				}

				@Override
				public void changedUpdate(DocumentEvent e)
				{
					if (tls != null && !tls.isEmpty())
					{
						for (TextListener tl : tls)
						{
							tl.textValueChanged(new TextEvent(text, TextEvent.TEXT_VALUE_CHANGED));
						}
					}
				}
			});
		}

		public void setFile(List<File> files)
		{
			fs.clear();
			if (files != null)
			{
				for (File f : files)
				{
					fs.add(f);
				}
			}
			String _fs = (fs + "");
			text.setText(_fs.substring(1, _fs.length() - 1));
		}

		public void setFile(File... files)
		{
			fs.clear();
			if (files != null)
			{
				for (File f : files)
				{
					fs.add(f);
				}
			}
			if (!fs.isEmpty())
			{
				String _fs = (fs + "");
				text.setText(_fs.substring(1, _fs.length() - 1));
			} else
			{
				text.setText("");
			}

		}

		public List<File> getFiles()
		{
			return fs;
		}

		public File getFile()
		{
			String src=text.getText();
			if (src==null||src.isEmpty())
			{
				return null;
			}
			return new File(src);
		}

		public String getText()
		{
			return text.getText();
		}

		public void setText(String txt)
		{
			if (txt != null)
			{
				String _fs[] = txt.split(",");
				for (String _f : _fs)
				{
					fs.add(new File(_f));
				}
			}
			text.setText(txt);
		}
 
		public String getToolTip()
		{
			return this.toolTip;
		}
		
	}

	public static class PCheckBox extends JCheckBox
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -5899194969416035642L;
		private Icon checkI;
		private Icon nocheckI;
		private String checks;
		private String nochecks;
		private ActionListener sL;
		private ActionListener dsL;

		public PCheckBox(String checks, String nochecks)
		{
			this(new MetalIconFactory.TreeControlIcon(true), new MetalIconFactory.TreeControlIcon(false), checks,
					nochecks);
		}

		public PCheckBox(Icon check, Icon nocheck, String checks, String nochecks)
		{
			super(nochecks, nocheck);
			init(check, nocheck, checks, nochecks);
		}

		public void setCheckListener(ActionListener l)
		{
			sL = l;
		}

		public void setDisCheckListener(ActionListener l)
		{
			dsL = l;
		}

		public void init(Icon check, Icon nocheck, String checks, String nochecks)
		{
			this.checkI = check;
			this.nocheckI = nocheck;
			this.checks = checks;
			this.nochecks = nochecks;
			this.sL = null;
			this.dsL = null;
			this.addActionListener(l -> {
				if (this.isSelected())
				{
					if (sL != null)
					{
						sL.actionPerformed(l);
					}
					this.setText(this.checks);
					this.setIcon(checkI);
				} else
				{
					if (dsL != null)
					{
						dsL.actionPerformed(l);
					}
					this.setText(this.nochecks);
					this.setIcon(nocheckI);
				}
			});
		}
	}

	/**
	 * 未有输入值时带提示的TextField
	 * 
	 * @author zhg
	 *
	 */
	public static class PTextField extends JTextField
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -5661466027682540464L;
		private String hint;
		private Color hintColor = Color.darkGray;

		public PTextField()
		{
			super();
		}

		public void setToolTipText(String text)
		{
			super.setToolTipText(text);
			setHint(text);
		}

		public String getHint()
		{
			return hint;
		}

		/**
		 * @param text
		 */
		public void setHint(String text)
		{
			this.hint = text;
		}

		public void setHintColor(Color c)
		{
			hintColor = c;
		}

		/**
		 * @return
		 */
		public Color getHintColor()
		{
			return hintColor;
		}

		protected void paintComponent(final Graphics pG)
		{
			super.paintComponent(pG);

			if (hint == null || hint.length() == 0 || getText().length() > 0)
			{
				return;
			}

			final Graphics2D g = (Graphics2D) pG;
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setColor(getHintColor());
			g.drawString(hint, getInsets().left, this.getHeight() / 2 + getInsets().top);
		}

	}

	public static class ExpandDialog extends JFrame
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -6260218070244295194L;
		private JTextArea txt;
		private JLabel label;

		public	ExpandDialog(Component position, int type)
		{
			super();
			switch (type)
			{
			case JOptionPane.ERROR_MESSAGE:
				init(position, loadImage("/icon/ic_error.png"), "错误", null, null);
				break;
			case JOptionPane.WARNING_MESSAGE:
				init(position, loadImage("/icon/ic_warn.png"), "警告", null, null);
				break;
			case JOptionPane.INFORMATION_MESSAGE:
				init(position, loadImage("/icon/ic_info.png"), "警告", null, null);
				break;
			default:
				init(null, null, null, null, null);
			}
		}

		public void setExpandContent(String msg)
		{
			txt.setText(msg);
		}

		public void setMessage(String msg)
		{
			label.setText(msg);
		}

		private void init(Component position, Image icon, String title, String msg, String content)
		{
			if (icon != null)
			{
				setIconImage(icon);
			}
			setSize(300, 150);
			setMinimumSize(new Dimension(300, 150));
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			if (position != null)
			{
				setLocationRelativeTo(position);
			} else
			{
				this.setLocationByPlatform(true);
			}
			setResizable(false);
			if (title != null)
			{
				setTitle(title);
			}
			JPanel contentPane = new JPanel(new BorderLayout(5, 5));
			setContentPane(contentPane);
			contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
			label = new JLabel(msg);
			contentPane.add(label, BorderLayout.NORTH);
			txt = new JTextArea();
			txt.setEditable(false);
			txt.setLineWrap(true);
			JScrollPane js = new JScrollPane(txt);
			js.setVisible(false);
			contentPane.add(js, BorderLayout.CENTER);
			if (content != null)
			{
				txt.setText(content);
			}
			JPanel bar = new JPanel();
			bar.setLayout(new BorderLayout());
			PCheckBox btn1 = new PCheckBox("隐藏详细信息", "显示详细信息");
			btn1.setCheckListener(l -> {
				setResizable(true);
				setSize(300, 300);
				js.setVisible(true);
			});
			btn1.setDisCheckListener(l -> {
				setSize(300, 150);
				setResizable(false);
				js.setVisible(false);
			});

			JButton btn2 = new JButton("确认");
			btn2.addActionListener(l -> {
				dispose();
			});
			bar.add(btn1, BorderLayout.LINE_START);
			bar.add(btn2, BorderLayout.LINE_END);
			contentPane.add(bar, BorderLayout.SOUTH);
		}
	}
}
