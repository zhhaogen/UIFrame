/**
 * 
 */
package cn.zhg.ui.base;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
/**
 * @author zhhaogen
 *
 * 创建于 2016年3月24日 上午1:01:50
 */
public class UISetting
{
	private static java.util.logging.Logger log = java.util.logging.Logger.getLogger(UISetting.class.getName());
	private static final String uiprop = "uisetting.txt";
	private static  Properties p =null;
	static
	{
		if(p==null)
		{
			load();
		}
	}
	/**
	 * 重新加载配置
	 */
	public static void load()
	{
		p = new Properties();
		try (FileInputStream is = new FileInputStream(uiprop))
		{
			p.load(is);
		} catch (IOException e)
		{
			log.log(Level.INFO, "读取UI设置失败", e);
		}
	}
	/**
	 * 保存内容
	 * @param keys
	 * @param values
	 */
	public static void put(String keys[],Object[] values)
	{
		if(keys==null||values==null)
		{
			return;
		}
		if(keys.length!=values.length)
		{
			throw new IllegalArgumentException("key和value的长度必须一致");
		}
		for(int i=0;i<keys.length;i++)
		{
			p.put(keys[i], values[i]);
		}
		save();
	}

	/**
	 * 保存内容
	 * @param key
	 * @param value
	 */
	public static void put(String key,Object value)
	{ 
		p.put(key, value);
		save();
	}
	public static String getString(String key)
	{
		return p.getProperty(key);
	}
	public static String getString(String key, String def)
	{
		return p.getProperty(key,def);
	}
	public static void save()
	{
		if(p!=null)
		{
			try (FileOutputStream out = new FileOutputStream(uiprop))
			{
				p.store(out, null);
			} catch (IOException e)
			{
				log.log(Level.INFO, "保存UI设置失败", e);
			}
		}
	}
}
