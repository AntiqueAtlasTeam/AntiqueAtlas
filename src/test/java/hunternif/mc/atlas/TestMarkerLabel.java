package hunternif.mc.atlas;

import hunternif.mc.atlas.marker.Marker;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.Locale;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

public class TestMarkerLabel {

    @Before
    public void setup() throws Exception {
        Locale locale = new Locale();

        Field localeProps = Locale.class.getDeclaredField("properties");
        localeProps.setAccessible(true);

        Map<String, String> translationMap = (Map<String, String>) localeProps.get(locale);
        translationMap.put("valid.key", "Translated string");
        translationMap.put("valid.key.param", "Translated string with param %s");

        Method setLocale = I18n.class.getDeclaredMethod("setLocale", Locale.class);
        setLocale.setAccessible(true);
        setLocale.invoke(null, locale);
    }

    @Test
    public void simple() {
        Marker marker = makeMarker("Label");
        Assert.assertEquals("Label", marker.getLocalizedLabel());

        marker = makeMarker("My label");
        Assert.assertEquals("My label", marker.getLocalizedLabel());

        marker = makeMarker("");
        Assert.assertEquals("", marker.getLocalizedLabel());
    }

    @Test
    public void specialCharacters() {
        Marker marker = makeMarker("100%");
        Assert.assertEquals("100%", marker.getLocalizedLabel());

        marker = makeMarker("100% aluminum");
        Assert.assertEquals("100% aluminum", marker.getLocalizedLabel());

        marker = makeMarker("%S55s%Sss%s5ss%%");
        Assert.assertEquals("%S55s%Sss%s5ss%%", marker.getLocalizedLabel());

        marker = makeMarker("invalid_%_key %s");
        Assert.assertEquals("invalid_%_key %s", marker.getLocalizedLabel());
    }

    @Test
    public void translated() {
        Marker marker = makeMarker("valid.key");
        Assert.assertEquals("Translated string", marker.getLocalizedLabel());

        // ignore parameter
        marker = makeMarker("valid.key param");
        Assert.assertEquals("Translated string", marker.getLocalizedLabel());

        marker = makeMarker("valid.key %s %d %f");
        Assert.assertEquals("Translated string", marker.getLocalizedLabel());
    }

    @Test
    public void translatedWithParam() {
        Marker marker = makeMarker("valid.key.param Name");
        Assert.assertEquals("Translated string with param Name", marker.getLocalizedLabel());

        marker = makeMarker("valid.key.param Name Surname");
        Assert.assertEquals("Translated string with param Name Surname", marker.getLocalizedLabel());

        // no parameters passed
        marker = makeMarker("valid.key.param");
        Assert.assertEquals("Translated string with param ", marker.getLocalizedLabel());
    }

    private static Marker makeMarker(String label) {
        return new Marker(1, "type", label, 1, 0, 0, true);
    }
}
