package com.p5solutions.trackstate.aop;

import java.lang.reflect.Method;
import java.util.Collection;
import junit.framework.TestCase;
import com.p5solutions.trackstate.utils.ReflectionUtility;

/**
 * TrackStateProxyFactoryTest:
 * 
 * @author Kasra Rasaee
 * @since 2009-02-11
 * 
 */
public class TrackStateProxyFactoryTest extends TestCase {
/*  
  public void testProxyFactory() {
    MyVO vo = new MyVO();
    vo.setFirstName("Bob");
    vo.setLastName("Robert");
    
    ProxyFactory factory = new ProxyFactory(vo);
    Object o = factory.getProxy();
    
    return;
  }
*/
  
 /*
  public void testSerializationClassLoader() {
    MyVO vo = new MyVO();
    vo.setFirstName("Bob");
    vo.setLastName("Robert");
    TrackStateProxyFactoryImpl impl = new TrackStateProxyFactoryImpl();
    MyVO proxyVO = impl.createProxy(MyVO.class, vo);
    try {
      FileOutputStream fos = new FileOutputStream("c:/object.out");
      ObjectOutputStream oos = new ObjectOutputStream(fos);
      oos.writeObject(proxyVO);
      oos.close();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
*/  
  /*
  public void testDeSerializationClassLoader() {
    try {
      FileInputStream fos = new FileInputStream("c:/object.out");
      ObjectInputStream oos = new ObjectInputStream(fos);
      Object o = oos.readObject();
      
      oos.close();
      
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
*/
  /**
   * 
   */
  public void testTrackStateProxyFactory() {

    MyVO vo = new MyVO();
    vo.setFirstName("Robert");
    assertEquals(vo.getFirstName(), "Robert");

    TrackStateProxyFactoryImpl impl = new TrackStateProxyFactoryImpl();
    MyVO proxyVO = impl.createProxy(MyVO.class);

    // set the target
    TrackStateProxy proxy = (TrackStateProxy) proxyVO;
    proxy.setTarget(vo);
    proxy.setInitialized(true);

    assertEquals(vo.getFirstName(), proxyVO.getFirstName());
    assertEquals(vo.getLastName(), proxyVO.getLastName());
    assertEquals(vo.getId(), proxyVO.getId());

    // lets check the laundry list
    proxyVO.setLastName("Jones");

    Collection<TrackStateLaundry> laundry = proxy.getTrackStateLaundryList();
    assertEquals(laundry.size(), 1);

    // set it back to its original state
    proxyVO.setLastName(null);

    // should be back to zero again
    assertEquals(laundry.size(), 0);

    proxyVO.setFirstName("Billy");
    proxyVO.setLastName("Bob");
    proxyVO.setId(19000L);

    // should be three state changes now
    assertEquals(laundry.size(), 3);

    Class<?> clazz = proxyVO.getClass();
    Method method = ReflectionUtility.findMethod(clazz, "getBirthDate");

    assertNotNull(method);
  }

}
