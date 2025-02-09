package micdoodle8.mods.galacticraft.core.client.gui.screen;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.text.Bidi;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

@SideOnly(Side.CLIENT)
public class SmallFontRenderer implements IResourceManagerReloadListener {

    private static final ResourceLocation[] field_111274_c = new ResourceLocation[256];

    /**
     * Array of width of all the characters in default.png
     */
    private final int[] charWidth = new int[256];

    /**
     * the height in pixels of default text
     */
    public int FONT_HEIGHT = 9;

    public Random fontRandom = new Random();

    /**
     * Array of the start/end column (in upper/lower nibble) for every glyph in the /font directory.
     */
    private final byte[] glyphWidth = new byte[65536];

    /**
     * Array of RGB triplets defining the 16 standard chat colors followed by 16 darker version of the same colors for
     * drop shadows.
     */
    private final int[] colorCode = new int[32];

    private final ResourceLocation field_111273_g;

    /**
     * The RenderEngine used to load and setup glyph textures.
     */
    private final TextureManager renderEngine;

    /**
     * Current X coordinate at which to draw the next character.
     */
    private float posX;

    /**
     * Current Y coordinate at which to draw the next character.
     */
    private float posY;

    /**
     * If true, strings should be rendered with Unicode fonts instead of the default.png font
     */
    private boolean unicodeFlag;

    /**
     * If true, the Unicode Bidirectional Algorithm should be run before rendering any string.
     */
    private boolean bidiFlag;

    /**
     * Used to specify new red value for the current color.
     */
    private float red;

    /**
     * Used to specify new blue value for the current color.
     */
    private float blue;

    /**
     * Used to specify new green value for the current color.
     */
    private float green;

    /**
     * Used to speify new alpha value for the current color.
     */
    private float alpha;

    /**
     * Text color of the currently rendering string.
     */
    private int textColor;

    /**
     * Set if the "k" style (random) is active in currently rendering string
     */
    private boolean randomStyle;

    /**
     * Set if the "l" style (bold) is active in currently rendering string
     */
    private boolean boldStyle;

    /**
     * Set if the "o" style (italic) is active in currently rendering string
     */
    private boolean italicStyle;

    /**
     * Set if the "n" style (underlined) is active in currently rendering string
     */
    private boolean underlineStyle;

    /**
     * Set if the "m" style (strikethrough) is active in currently rendering string
     */
    private boolean strikethroughStyle;

    public SmallFontRenderer(GameSettings par1GameSettings, ResourceLocation par2ResourceLocation,
        TextureManager par3TextureManager, boolean par4) {
        this.field_111273_g = par2ResourceLocation;
        this.renderEngine = par3TextureManager;
        this.unicodeFlag = true;

        for (int i = 0; i < 32; ++i) {
            final int j = (i >> 3 & 1) * 85;
            int k = (i >> 2 & 1) * 170 + j;
            int l = (i >> 1 & 1) * 170 + j;
            int i1 = (i & 1) * 170 + j;

            if (i == 6) {
                k += 85;
            }

            if (par1GameSettings.anaglyph) {
                final int j1 = (k * 30 + l * 59 + i1 * 11) / 100;
                final int k1 = (k * 30 + l * 70) / 100;
                final int l1 = (k * 30 + i1 * 70) / 100;
                k = j1;
                l = k1;
                i1 = l1;
            }

            if (i >= 16) {
                k /= 4;
                l /= 4;
                i1 /= 4;
            }

            this.colorCode[i] = (k & 255) << 16 | (l & 255) << 8 | i1 & 255;
        }

        this.readGlyphSizes();
    }

    private void func_111272_d() {
        BufferedImage bufferedimage;

        try {
            bufferedimage = ImageIO.read(
                Minecraft.getMinecraft()
                    .getResourceManager()
                    .getResource(this.field_111273_g)
                    .getInputStream());
        } catch (final IOException ioexception) {
            throw new RuntimeException(ioexception);
        }

        final int i = bufferedimage.getWidth();
        final int j = bufferedimage.getHeight();
        final int[] aint = new int[i * j];
        bufferedimage.getRGB(0, 0, i, j, aint, 0, i);
        final int k = j / 16;
        final int l = i / 16;
        final byte b0 = 1;
        final float f = 8.0F / l;
        int i1 = 0;

        while (i1 < 256) {
            final int j1 = i1 % 16;
            final int k1 = i1 / 16;

            if (i1 == 32) {
                this.charWidth[i1] = 3 + b0;
            }

            int l1 = l - 1;

            while (true) {
                if (l1 >= 0) {
                    final int i2 = j1 * l + l1;
                    boolean flag = true;

                    for (int j2 = 0; j2 < k && flag; ++j2) {
                        final int k2 = (k1 * l + j2) * i;

                        if ((aint[i2 + k2] >> 24 & 255) != 0) {
                            flag = false;
                            break;
                        }
                    }

                    if (flag) {
                        --l1;
                        continue;
                    }
                }

                ++l1;
                this.charWidth[i1] = (int) (0.5D + l1 * f) + b0;
                ++i1;
                break;
            }
        }
    }

    private void readGlyphSizes() {
        try {
            final InputStream inputstream = Minecraft.getMinecraft()
                .getResourceManager()
                .getResource(new ResourceLocation("font/glyph_sizes.bin"))
                .getInputStream();
            inputstream.read(this.glyphWidth);
        } catch (final IOException ioexception) {
            throw new RuntimeException(ioexception);
        }
    }

    /**
     * Pick how to render a single character and return the width used.
     */
    private float renderCharAtPos(int par1, char par2, boolean par3) {
        return par2 == 32 ? 4.0F
            : par1 > 0 && !this.unicodeFlag ? this.renderDefaultChar(par1 + 32, par3)
                : this.renderUnicodeChar(par2, par3);
    }

    /**
     * Render a single character with the default.png font at current (posX,posY) location...
     */
    private float renderDefaultChar(int par1, boolean par2) {
        final float f = par1 % 16 * 8;
        final float f1 = par1 / 16 * 8;
        final float f2 = par2 ? 1.0F : 0.0F;
        this.renderEngine.bindTexture(this.field_111273_g);
        final float f3 = this.charWidth[par1] - 0.01F;
        GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
        GL11.glTexCoord2f(f / 128.0F, f1 / 128.0F);
        GL11.glVertex3f(this.posX + f2, this.posY, 0.0F);
        GL11.glTexCoord2f(f / 128.0F, (f1 + 7.99F) / 128.0F);
        GL11.glVertex3f(this.posX - f2, this.posY + 7.99F, 0.0F);
        GL11.glTexCoord2f((f + f3 - 1.0F) / 128.0F, f1 / 128.0F);
        GL11.glVertex3f(this.posX + f3 - 1.0F + f2, this.posY, 0.0F);
        GL11.glTexCoord2f((f + f3 - 1.0F) / 128.0F, (f1 + 7.99F) / 128.0F);
        GL11.glVertex3f(this.posX + f3 - 1.0F - f2, this.posY + 7.99F, 0.0F);
        GL11.glEnd();
        return this.charWidth[par1];
    }

    private ResourceLocation func_111271_a(int par1) {
        if (SmallFontRenderer.field_111274_c[par1] == null) {
            SmallFontRenderer.field_111274_c[par1] = new ResourceLocation(
                String.format("textures/font/unicode_page_%02x.png", Integer.valueOf(par1)));
        }

        return SmallFontRenderer.field_111274_c[par1];
    }

    /**
     * Load one of the /font/glyph_XX.png into a new GL texture and store the texture ID in glyphTextureName array.
     */
    private void loadGlyphTexture(int par1) {
        this.renderEngine.bindTexture(this.func_111271_a(par1));
    }

    /**
     * Render a single Unicode character at current (posX,posY) location using one of the /font/glyph_XX.png files...
     */
    private float renderUnicodeChar(char par1, boolean par2) {
        if (this.glyphWidth[par1] == 0) {
            return 0.0F;
        }
        final int i = par1 / 256;
        this.loadGlyphTexture(i);
        final int j = this.glyphWidth[par1] >>> 4;
        final int k = this.glyphWidth[par1] & 15;
        final float f1 = k + 1;
        final float f2 = par1 % 16 * 16 + j;
        final float f3 = (par1 & 255) / 16 * 16;
        final float f4 = f1 - j - 0.02F;
        final float f5 = par2 ? 1.0F : 0.0F;
        GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
        GL11.glTexCoord2f(f2 / 256.0F, f3 / 256.0F);
        GL11.glVertex3f(this.posX + f5, this.posY, 0.0F);
        GL11.glTexCoord2f(f2 / 256.0F, (f3 + 15.98F) / 256.0F);
        GL11.glVertex3f(this.posX - f5, this.posY + 7.99F, 0.0F);
        GL11.glTexCoord2f((f2 + f4) / 256.0F, f3 / 256.0F);
        GL11.glVertex3f(this.posX + f4 / 2.0F + f5, this.posY, 0.0F);
        GL11.glTexCoord2f((f2 + f4) / 256.0F, (f3 + 15.98F) / 256.0F);
        GL11.glVertex3f(this.posX + f4 / 2.0F - f5, this.posY + 7.99F, 0.0F);
        GL11.glEnd();
        return (f1 - j) / 2.0F + 1.0F;
    }

    /**
     * Draws the specified string with a shadow.
     */
    public int drawStringWithShadow(String par1Str, int par2, int par3, int par4) {
        return this.drawString(par1Str, par2, par3, par4, true);
    }

    /**
     * Draws the specified string.
     */
    public int drawString(String par1Str, int par2, int par3, int par4) {
        return this.drawString(par1Str, par2, par3, par4, false);
    }

    /**
     * Draws the specified string. Args: string, x, y, color, dropShadow
     */
    public int drawString(String par1Str, int par2, int par3, int par4, boolean par5) {
        this.resetStyles();

        if (this.bidiFlag) {
            par1Str = this.bidiReorder(par1Str);
        }

        int l;

        if (par5) {
            l = this.renderString(par1Str, par2 + 1, par3 + 1, par4, true);
            l = Math.max(l, this.renderString(par1Str, par2, par3, par4, false));
        } else {
            l = this.renderString(par1Str, par2, par3, par4, false);
        }

        return l;
    }

    /**
     * Apply Unicode Bidirectional Algorithm to string and return a new possibly reordered string for visual rendering.
     */
    private String bidiReorder(String par1Str) {
        if (par1Str == null || !Bidi.requiresBidi(par1Str.toCharArray(), 0, par1Str.length())) {
            return par1Str;
        }
        final Bidi bidi = new Bidi(par1Str, -2);
        final byte[] abyte = new byte[bidi.getRunCount()];
        final String[] astring = new String[abyte.length];
        int i;

        for (int j = 0; j < abyte.length; ++j) {
            final int k = bidi.getRunStart(j);
            i = bidi.getRunLimit(j);
            final int l = bidi.getRunLevel(j);
            final String s1 = par1Str.substring(k, i);
            abyte[j] = (byte) l;
            astring[j] = s1;
        }

        final String[] astring1 = astring.clone();
        Bidi.reorderVisually(abyte, 0, astring, 0, abyte.length);
        final StringBuilder stringbuilder = new StringBuilder();
        i = 0;

        while (i < astring.length) {
            byte b0 = abyte[i];
            int i1 = 0;

            while (true) {
                if (i1 < astring1.length) {
                    if (!astring1[i1].equals(astring[i])) {
                        ++i1;
                        continue;
                    }

                    b0 = abyte[i1];
                }

                if ((b0 & 1) == 0) {
                    stringbuilder.append(astring[i]);
                } else {
                    for (i1 = astring[i].length() - 1; i1 >= 0; --i1) {
                        char c0 = astring[i].charAt(i1);

                        if (c0 == 40) {
                            c0 = 41;
                        } else if (c0 == 41) {
                            c0 = 40;
                        }

                        stringbuilder.append(c0);
                    }
                }

                ++i;
                break;
            }
        }

        return stringbuilder.toString();
    }

    /**
     * Reset all style flag fields in the class to false; called at the start of string rendering
     */
    private void resetStyles() {
        this.randomStyle = false;
        this.boldStyle = false;
        this.italicStyle = false;
        this.underlineStyle = false;
        this.strikethroughStyle = false;
    }

    /**
     * Render a single line string at the current (posX,posY) and update posX
     */
    private void renderStringAtPos(String par1Str, boolean par2) {
        for (int i = 0; i < par1Str.length(); ++i) {
            final char c0 = par1Str.charAt(i);
            int j;
            int k;

            if (c0 == 167 && i + 1 < par1Str.length()) {
                j = "0123456789abcdefklmnor".indexOf(
                    par1Str.toLowerCase()
                        .charAt(i + 1));

                if (j < 16) {
                    this.randomStyle = false;
                    this.boldStyle = false;
                    this.strikethroughStyle = false;
                    this.underlineStyle = false;
                    this.italicStyle = false;

                    if (j < 0 || j > 15) {
                        j = 15;
                    }

                    if (par2) {
                        j += 16;
                    }

                    k = this.colorCode[j];
                    this.textColor = k;
                    GL11.glColor4f((k >> 16) / 255.0F, (k >> 8 & 255) / 255.0F, (k & 255) / 255.0F, this.alpha);
                } else {
                    switch (j) {
                        case 16:
                            this.randomStyle = true;
                            break;
                        case 17:
                            this.boldStyle = true;
                            break;
                        case 18:
                            this.strikethroughStyle = true;
                            break;
                        case 19:
                            this.underlineStyle = true;
                            break;
                        case 20:
                            this.italicStyle = true;
                            break;
                        case 21:
                            this.randomStyle = false;
                            this.boldStyle = false;
                            this.strikethroughStyle = false;
                            this.underlineStyle = false;
                            this.italicStyle = false;
                            GL11.glColor4f(this.red, this.blue, this.green, this.alpha);
                            break;
                        default:
                            break;
                    }
                }

                ++i;
            } else {
                j = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000"
                    .indexOf(c0);

                if (this.randomStyle && j != -1) {
                    do {
                        k = this.fontRandom.nextInt(this.charWidth.length);
                    } while (this.charWidth[j] != this.charWidth[k]);

                    j = k;
                }

                final float f = this.unicodeFlag ? 0.5F : 1.0F;
                final boolean flag1 = (c0 == 0 || j == -1 || this.unicodeFlag) && par2;

                if (flag1) {
                    this.posX -= f;
                    this.posY -= f;
                }

                float f1 = this.renderCharAtPos(j, c0, this.italicStyle);

                if (flag1) {
                    this.posX += f;
                    this.posY += f;
                }

                if (this.boldStyle) {
                    this.posX += f;

                    if (flag1) {
                        this.posX -= f;
                        this.posY -= f;
                    }

                    this.renderCharAtPos(j, c0, this.italicStyle);
                    this.posX -= f;

                    if (flag1) {
                        this.posX += f;
                        this.posY += f;
                    }

                    ++f1;
                }

                Tessellator tessellator;

                if (this.strikethroughStyle) {
                    tessellator = Tessellator.instance;
                    GL11.glDisable(GL11.GL_TEXTURE_2D);
                    tessellator.startDrawingQuads();
                    tessellator.addVertex(this.posX, this.posY + this.FONT_HEIGHT / 2, 0.0D);
                    tessellator.addVertex(this.posX + f1, this.posY + this.FONT_HEIGHT / 2, 0.0D);
                    tessellator.addVertex(this.posX + f1, this.posY + this.FONT_HEIGHT / 2 - 1.0F, 0.0D);
                    tessellator.addVertex(this.posX, this.posY + this.FONT_HEIGHT / 2 - 1.0F, 0.0D);
                    tessellator.draw();
                    GL11.glEnable(GL11.GL_TEXTURE_2D);
                }

                if (this.underlineStyle) {
                    tessellator = Tessellator.instance;
                    GL11.glDisable(GL11.GL_TEXTURE_2D);
                    tessellator.startDrawingQuads();
                    final int l = this.underlineStyle ? -1 : 0;
                    tessellator.addVertex(this.posX + l, this.posY + this.FONT_HEIGHT, 0.0D);
                    tessellator.addVertex(this.posX + f1, this.posY + this.FONT_HEIGHT, 0.0D);
                    tessellator.addVertex(this.posX + f1, this.posY + this.FONT_HEIGHT - 1.0F, 0.0D);
                    tessellator.addVertex(this.posX + l, this.posY + this.FONT_HEIGHT - 1.0F, 0.0D);
                    tessellator.draw();
                    GL11.glEnable(GL11.GL_TEXTURE_2D);
                }

                this.posX += (int) f1;
            }
        }
    }

    /**
     * Render string either left or right aligned depending on bidiFlag
     */
    private int renderStringAligned(String par1Str, int par2, int par3, int par4, int par5, boolean par6) {
        if (this.bidiFlag) {
            par1Str = this.bidiReorder(par1Str);
            final int i1 = this.getStringWidth(par1Str);
            par2 = par2 + par4 - i1;
        }

        return this.renderString(par1Str, par2, par3, par5, par6);
    }

    /**
     * Render single line string by setting GL color, current (posX,posY), and calling renderStringAtPos()
     */
    private int renderString(String par1Str, int par2, int par3, int par4, boolean par5) {
        if (par1Str == null) {
            return 0;
        }
        if ((par4 & -67108864) == 0) {
            par4 |= -16777216;
        }

        if (par5) {
            par4 = (par4 & 16579836) >> 2 | par4 & -16777216;
        }

        this.red = (par4 >> 16 & 255) / 255.0F;
        this.blue = (par4 >> 8 & 255) / 255.0F;
        this.green = (par4 & 255) / 255.0F;
        this.alpha = (par4 >> 24 & 255) / 255.0F;
        GL11.glColor4f(this.red, this.blue, this.green, this.alpha);
        this.posX = par2;
        this.posY = par3;
        this.renderStringAtPos(par1Str, par5);
        return (int) this.posX;
    }

    /**
     * Returns the width of this string. Equivalent of FontMetrics.stringWidth(String s).
     */
    public int getStringWidth(String par1Str) {
        if (par1Str == null) {
            return 0;
        }
        int i = 0;
        boolean flag = false;

        for (int j = 0; j < par1Str.length(); ++j) {
            char c0 = par1Str.charAt(j);
            int k = this.getCharWidth(c0);

            if (k < 0 && j < par1Str.length() - 1) {
                ++j;
                c0 = par1Str.charAt(j);

                if (c0 != 108 && c0 != 76) {
                    if (c0 == 114 || c0 == 82) {
                        flag = false;
                    }
                } else {
                    flag = true;
                }

                k = 0;
            }

            i += k;

            if (flag) {
                ++i;
            }
        }

        return i;
    }

    /**
     * Returns the width of this character as rendered.
     */
    public int getCharWidth(char par1) {
        if (par1 == 167) {
            return -1;
        }
        if (par1 == 32) {
            return 4;
        }
        final int i = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000"
            .indexOf(par1);

        if (par1 > 0 && i != -1 && !this.unicodeFlag) {
            return this.charWidth[i];
        } else if (this.glyphWidth[par1] != 0) {
            int j = this.glyphWidth[par1] >>> 4;
            int k = this.glyphWidth[par1] & 15;

            if (k > 7) {
                k = 15;
                j = 0;
            }

            ++k;
            return (k - j) / 2 + 1;
        } else {
            return 0;
        }
    }

    /**
     * Trims a string to fit a specified Width.
     */
    public String trimStringToWidth(String par1Str, int par2) {
        return this.trimStringToWidth(par1Str, par2, false);
    }

    /**
     * Trims a string to a specified width, and will reverse it if par3 is set.
     */
    public String trimStringToWidth(String par1Str, int par2, boolean par3) {
        final StringBuilder stringbuilder = new StringBuilder();
        int j = 0;
        final int k = par3 ? par1Str.length() - 1 : 0;
        final int l = par3 ? -1 : 1;
        boolean flag1 = false;
        boolean flag2 = false;

        for (int i1 = k; i1 >= 0 && i1 < par1Str.length() && j < par2; i1 += l) {
            final char c0 = par1Str.charAt(i1);
            final int j1 = this.getCharWidth(c0);

            if (flag1) {
                flag1 = false;

                if (c0 != 108 && c0 != 76) {
                    if (c0 == 114 || c0 == 82) {
                        flag2 = false;
                    }
                } else {
                    flag2 = true;
                }
            } else if (j1 < 0) {
                flag1 = true;
            } else {
                j += j1;

                if (flag2) {
                    ++j;
                }
            }

            if (j > par2) {
                break;
            }

            if (par3) {
                stringbuilder.insert(0, c0);
            } else {
                stringbuilder.append(c0);
            }
        }

        return stringbuilder.toString();
    }

    /**
     * Remove all newline characters from the end of the string
     */
    private String trimStringNewline(String par1Str) {
        while (par1Str != null && par1Str.endsWith("\n")) {
            par1Str = par1Str.substring(0, par1Str.length() - 1);
        }

        return par1Str;
    }

    /**
     * Splits and draws a String with wordwrap (maximum length is parameter k)
     */
    public void drawSplitString(String par1Str, int par2, int par3, int par4, int par5) {
        this.resetStyles();
        this.textColor = par5;
        par1Str = this.trimStringNewline(par1Str);
        this.renderSplitString(par1Str, par2, par3, par4, false);
    }

    /**
     * Perform actual work of rendering a multi-line string with wordwrap and with darker drop shadow color if flag is
     * set
     */
    private void renderSplitString(String par1Str, int par2, int par3, int par4, boolean par5) {
        final List<?> list = this.listFormattedStringToWidth(par1Str, par4);

        for (final Iterator<?> iterator = list.iterator(); iterator.hasNext(); par3 += this.FONT_HEIGHT) {
            final String s1 = (String) iterator.next();
            this.renderStringAligned(s1, par2, par3, par4, this.textColor, par5);
        }
    }

    /**
     * Returns the width of the wordwrapped String (maximum length is parameter k)
     */
    public int splitStringWidth(String par1Str, int par2) {
        return this.FONT_HEIGHT * this.listFormattedStringToWidth(par1Str, par2)
            .size();
    }

    /**
     * Set unicodeFlag controlling whether strings should be rendered with Unicode fonts instead of the default.png
     * font.
     */
    public void setUnicodeFlag(boolean par1) {
        this.unicodeFlag = par1;
    }

    /**
     * Get unicodeFlag controlling whether strings should be rendered with Unicode fonts instead of the default.png
     * font.
     */
    public boolean getUnicodeFlag() {
        return this.unicodeFlag;
    }

    /**
     * Set bidiFlag to control if the Unicode Bidirectional Algorithm should be run before rendering any string.
     */
    public void setBidiFlag(boolean par1) {
        this.bidiFlag = par1;
    }

    /**
     * Breaks a string into a list of pieces that will fit a specified width.
     */
    public List<String> listFormattedStringToWidth(String par1Str, int par2) {
        return Arrays.asList(
            this.wrapFormattedStringToWidth(par1Str, par2)
                .split("\n"));
    }

    /**
     * Inserts newline and formatting into a string to wrap it within the specified width.
     */
    String wrapFormattedStringToWidth(String par1Str, int par2) {
        final int j = this.sizeStringToWidth(par1Str, par2);

        if (par1Str.length() <= j) {
            return par1Str;
        }
        final String s1 = par1Str.substring(0, j);
        final char c0 = par1Str.charAt(j);
        final boolean flag = c0 == 32 || c0 == 10;
        final String s2 = SmallFontRenderer.getFormatFromString(s1) + par1Str.substring(j + (flag ? 1 : 0));
        return s1 + "\n" + this.wrapFormattedStringToWidth(s2, par2);
    }

    /**
     * Determines how many characters from the string will fit into the specified width.
     */
    private int sizeStringToWidth(String par1Str, int par2) {
        final int j = par1Str.length();
        int k = 0;
        int l = 0;
        int i1 = -1;

        for (boolean flag = false; l < j; ++l) {
            final char c0 = par1Str.charAt(l);

            switch (c0) {
                case 10:
                    --l;
                    break;
                case 167:
                    if (l < j - 1) {
                        ++l;
                        final char c1 = par1Str.charAt(l);

                        if (c1 != 108 && c1 != 76) {
                            if (c1 == 114 || c1 == 82 || SmallFontRenderer.isFormatColor(c1)) {
                                flag = false;
                            }
                        } else {
                            flag = true;
                        }
                    }

                    break;
                case 32:
                    i1 = l;
                default:
                    k += this.getCharWidth(c0);

                    if (flag) {
                        ++k;
                    }
            }

            if (c0 == 10) {
                ++l;
                i1 = l;
                break;
            }

            if (k > par2) {
                break;
            }
        }

        return l != j && i1 != -1 && i1 < l ? i1 : l;
    }

    /**
     * Checks if the char code is a hexadecimal character, used to set colour.
     */
    private static boolean isFormatColor(char par0) {
        return par0 >= 48 && par0 <= 57 || par0 >= 97 && par0 <= 102 || par0 >= 65 && par0 <= 70;
    }

    /**
     * Checks if the char code is O-K...lLrRk-o... used to set special formatting.
     */
    private static boolean isFormatSpecial(char par0) {
        return par0 >= 107 && par0 <= 111 || par0 >= 75 && par0 <= 79 || par0 == 114 || par0 == 82;
    }

    /**
     * Digests a string for nonprinting formatting characters then returns a string containing only that formatting.
     */
    private static String getFormatFromString(String par0Str) {
        String s1 = "";
        int i = -1;
        final int j = par0Str.length();

        while ((i = par0Str.indexOf(167, i + 1)) != -1) {
            if (i < j - 1) {
                final char c0 = par0Str.charAt(i + 1);

                if (SmallFontRenderer.isFormatColor(c0)) {
                    s1 = "\u00a7" + c0;
                } else if (SmallFontRenderer.isFormatSpecial(c0)) {
                    s1 = s1 + "\u00a7" + c0;
                }
            }
        }

        return s1;
    }

    /**
     * Get bidiFlag that controls if the Unicode Bidirectional Algorithm should be run before rendering any string
     */
    public boolean getBidiFlag() {
        return this.bidiFlag;
    }

    @Override
    public void onResourceManagerReload(IResourceManager var1) {
        this.func_111272_d();
    }
}
