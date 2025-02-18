package com.client.clickgui.screens;

import com.client.BloodyClient;
import com.client.alt.Account;
import com.client.alt.AccountUtils;
import com.client.alt.Accounts;
import com.client.impl.function.client.ClickGui;
import com.client.system.function.FunctionManager;
import com.client.system.textures.DownloadImage;
import com.client.utils.Utils;
import com.client.utils.files.SoundManager;
import com.client.utils.math.Timer;
import com.client.utils.math.animation.AnimationUtils;
import com.client.utils.math.rect.FloatRect;
import com.client.utils.misc.CustomSoundInstance;
import com.client.utils.render.wisetree.font.api.FontRenderer;
import com.client.utils.render.wisetree.font.main.IFont;
import com.client.utils.render.wisetree.render.render2d.main.GL;
import com.client.utils.render.wisetree.render.render2d.utils.ScissorUtils;
import com.client.utils.render.wisetree.render.render2d.utils.shader.shaders.BlurShader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.client.BloodyClient.mc;

public class AltScreen extends Screen {
    private static AltScreen instance;

    public static AltScreen getInstance() {
        if (instance == null) {
            instance = new AltScreen();
            instance.initka();
        }

        instance.animation = 0;

        return instance;
    }

    public AltScreen() {
        super(Text.of(""));
    }

    private List<AltElement> elementList = new ArrayList<>();
    private double oldWidth, oldHeight;
    private float scroll, targetScroll, amount, animation;

    private String currentNickname = "",
            currentError = "Ошибка!";
    private boolean isSelected = false,
            isError = false,
            isUpdating = false;

    public long error = 0;
    private final Timer dot = new Timer();

    private FloatRect mainRect = new FloatRect(0, 0,0,0),
            inputRect = new FloatRect(0, 0, 0, 0),
            innerRect = new FloatRect(0, 0,0,0),
            accountsRect = new FloatRect(0, 0,0,0),
            addButtonRect = new FloatRect(0, 0,0,0),
            randButtonRect = new FloatRect(0, 0,0,0),
            sliderRect = new FloatRect(0, 0,0,0);

    private boolean closing = false;
    private Runnable postTask = () -> {};

    public void updateAccounts() {
        isUpdating = true;
        elementList.clear();

        for (Account account : Accounts.getAccounts()) {
            elementList.add(new AltElement(new FloatRect(accountsRect.getX() + 2.5, accountsRect.getY(), accountsRect.getW() - 5, 25), account));
        }
    }

    public boolean isLatinLetter(char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || c == '_';
    }

    public static String getStringIgnoreLastChar(String str) {
        StringBuilder s = new StringBuilder();

        for (int i = 0; i < str.toCharArray().length - 1; i++) {
            s.append(str.toCharArray()[i]);
        }

        return s.toString();
    }

    public void addAccount() {
        if (currentNickname.length() <= 3) {
            currentError = (Utils.isRussianLanguage ? "Минимальная длина 4 символа" : "Minimal length is 4");
            isError = true;
            error = System.currentTimeMillis();
        } else {
            Accounts.add(currentNickname, false);
            currentNickname = "";
            isSelected = false;
        }
    }

    public float getScrollerHeight() {
        float favoriteY = 3;

        List<AltElement> favouriteAlts = elementList.stream()
                .filter(e -> e.account.isFavorite)
                .collect(Collectors.toList());

        if (!favouriteAlts.isEmpty()) {
            IFont.draw(IFont.MONTSERRAT_MEDIUM, (Utils.isRussianLanguage ? "Избранное" : "Favourite"), accountsRect.getX() + 5, accountsRect.getY() + 3, Color.ORANGE, 8);

            favoriteY += IFont.getHeight(IFont.MONTSERRAT_MEDIUM, (Utils.isRussianLanguage ? "Избранное" : "Favourite"), 8) + 2;

            favouriteAlts.sort(Comparator.comparing(e -> e.account.name));

            for (AltElement altElement : favouriteAlts) {
                favoriteY += altElement.rect.getH() + 3;
            }
        }

        List<AltElement> nonFavouriteAlts = elementList.stream()
                .filter(e -> !e.account.isFavorite)
                .collect(Collectors.toList());

        float nonFavoriteY = favoriteY;

        if (!nonFavouriteAlts.isEmpty()) {
            IFont.draw(IFont.MONTSERRAT_MEDIUM, (Utils.isRussianLanguage ? "Остальное" : "Other"), accountsRect.getX() + 5, favoriteY + (favouriteAlts.isEmpty() ? 0 : 7.5f), new Color(162, 162, 162).brighter(), 8);

            nonFavoriteY += (favouriteAlts.isEmpty() ? 0 : 7.5f) + IFont.getHeight(IFont.MONTSERRAT_MEDIUM, (Utils.isRussianLanguage ? "Остальное" : "Other"), 8) + 2;

            nonFavouriteAlts.sort(Comparator.comparing(e -> e.account.name));

            for (AltElement altElement : nonFavouriteAlts) {
                nonFavoriteY += altElement.rect.getH() + 3;
            }
        }

        return nonFavoriteY;
    }

    private void scroll() {
        if (isUpdating) {
            isUpdating = false;
            return;
        }

        if (Accounts.getAccounts().isEmpty() || getScrollerHeight() < accountsRect.getH()) {
            targetScroll = 0;
        } else {
            List<AltElement> favouriteAlts = elementList.stream()
                    .filter(e -> e.account.isFavorite)
                    .collect(Collectors.toList());
            favouriteAlts.sort(Comparator.comparing(e -> e.account.name));

            List<AltElement> nonFavouriteAlts = elementList.stream()
                    .filter(e -> !e.account.isFavorite)
                    .collect(Collectors.toList());
            nonFavouriteAlts.sort(Comparator.comparing(e -> e.account.name));

            AltElement first, second;
            if (!favouriteAlts.isEmpty())
                first = favouriteAlts.get(0);
            else first = nonFavouriteAlts.get(0);

            if (!nonFavouriteAlts.isEmpty())
                second = nonFavouriteAlts.get(nonFavouriteAlts.size() - 1);
            else second = favouriteAlts.get(favouriteAlts.size() - 1);

            if (first.rect.getY() - (IFont.getHeight(IFont.MONTSERRAT_MEDIUM, (Utils.isRussianLanguage ? "Избранное" : "Favourite"), 8) + 2) > accountsRect.getY() + 3) {
                targetScroll = -3;
            } else if (second.rect.getY2() < accountsRect.getY2() - 3) {
                targetScroll = -(getScrollerHeight() - accountsRect.getH()) + 3;
            } else if (amount != 0) {
                targetScroll += amount * 20;
                amount = 0;
            }
        }

        scroll = AnimationUtils.fast(scroll, targetScroll);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX1, int mouseY1, float partialTicks) {
        if (oldWidth != mc.getWindow().getWidth() || oldHeight != mc.getWindow().getHeight()) initka();

        if (animation != 1000 && !closing) animation = AnimationUtils.fast(animation, 1000);
        else if (animation != 2000 && closing) animation = AnimationUtils.fast(animation, 2000);

        if (animation >= 1800 && closing) {
            closing = false;
            postTask.run();
        }

        mainRect.setX((float) (mc.getWindow().getWidth() / 4 - 150 - 6 + 1000 - animation));
        inputRect.setX((float) (mc.getWindow().getWidth() / 4 - 150 + 1000 - animation));
        updateRects();
        elementList.forEach(e -> e.rect.setX((float) (accountsRect.getX() + 2.5 + 1000 - animation)));

        if (getScrollerHeight() > accountsRect.getH()) sliderRect.setH(accountsRect.getH() / getScrollerHeight() * accountsRect.getH());
        else sliderRect.setH(accountsRect.getH());

        if (getScrollerHeight() > accountsRect.getH()) sliderRect.setY(accountsRect.getY() + ((scroll / (-(getScrollerHeight() - accountsRect.getH()) + 3)) * (accountsRect.getH() - (accountsRect.getH() / getScrollerHeight() * accountsRect.getH()))));
        else sliderRect.setY(accountsRect.getY());

        if (AccountUtils.shouldUpdate) {
            updateAccounts();
            AccountUtils.shouldUpdate = false;
        }

        if (isError && System.currentTimeMillis() - error > 1500) {
            isError = false;
        }

        dot.tick();
        dot.resetIfPassed(20);

        scroll();

        int mouseX = (int) (BloodyClient.mc.mouse.getX() / 2);
        int mouseY = (int) (BloodyClient.mc.mouse.getY() / 2);

        Utils.rescaling(() -> {
            GL.prepare();
            GL.drawRoundedTexture(DownloadImage.getGlId(DownloadImage.CHRISTMAS_MENU), 0, 0, mc.getWindow().getScaledWidth(), mc.getWindow().getScaledHeight(), 0);
            GL.end();

            BlurShader.registerRenderCall(() -> {
                GL.drawRoundedRect(new FloatRect(0, 0, mc.getWindow().getScaledWidth(), mc.getWindow().getScaledHeight()), 0, Color.WHITE);
            });

            BlurShader.draw(8);

            GL.prepare();
            GL.drawRoundedGlowRect(mainRect, 4, 3, new Color(24, 26, 33, 255).brighter());

            GL.drawRoundedRect(inputRect, 4, new Color(24, 26, 33, 150));
            GL.drawRoundedRect(addButtonRect, 4,  new Color(24, 26, 33, 150));
            GL.drawRoundedTexture(DownloadImage.getGlId(DownloadImage.ROUNDED_PLUS), new FloatRect(addButtonRect.getCenteredX() - 6, addButtonRect.getCenteredY() - 6, 12, 12), 0);

            GL.drawRoundedRect(randButtonRect, 4, new Color(24, 26, 33, 150));
            GL.drawRoundedTexture(DownloadImage.getGlId(DownloadImage.REFRESH), new FloatRect(randButtonRect.getCenteredX() - 6, randButtonRect.getCenteredY() - 6, 12, 12), 0);

            GL.drawRoundedRect(innerRect, 4, new Color(24, 26, 33, 150));

            GL.drawRoundedRect(sliderRect, 2, new Color(162, 162, 162).brighter());
            //GL.drawRoundedRect(accountsRect, 4, Color.BLACK);
            GL.end();

            IFont.drawCenteredY(IFont.MONTSERRAT_MEDIUM, isError ? currentError : (isSelected ? currentNickname + (dot.passed(10) && currentNickname.length() < 16 ? "|" : "") : (Utils.isRussianLanguage ? "Введите желаемое имя" : "Enter a name")), inputRect.getX() + 5, inputRect.getCenteredY(), isError ? Color.RED.darker() : (isSelected ? Color.WHITE : new Color(162, 162, 162)), 8);

            float w = IFont.getWidth(IFont.MONTSERRAT_MEDIUM, (Utils.isRussianLanguage ? "Ваш никнейм: " : "Your nickname: ") + mc.getSession().getUsername(), 8);
            float h = IFont.getHeight(IFont.MONTSERRAT_MEDIUM, "|", 9);
            IFont.draw(IFont.MONTSERRAT_MEDIUM, (Utils.isRussianLanguage ? "Ваш никнейм: " : "Your nickname: "), accountsRect.getCenteredX() - w / 2, mainRect.getY() - h - 3 - 2, new Color(162, 162, 162).brighter(), 8);

            FontRenderer.color(true);
            FontRenderer.shouldRename(false);
            IFont.draw(IFont.MONTSERRAT_MEDIUM, mc.getSession().getUsername(), accountsRect.getCenteredX() - w / 2 + IFont.getWidth(IFont.MONTSERRAT_MEDIUM, (Utils.isRussianLanguage ? "Ваш никнейм: " : "Your nickname: "), 8), mainRect.getY() - h - 3 - 2, new Color(162, 162, 162).brighter(), 8);
            FontRenderer.color(false);
            FontRenderer.shouldRename(true);

            ScissorUtils.push();
            ScissorUtils.setFromComponentCoordinates(accountsRect.getX(),
                    accountsRect.getY(),
                    accountsRect.getW(),
                    accountsRect.getH()
            );

            float favoriteY = accountsRect.getY() + 3 + scroll;

            List<AltElement> favouriteAlts = elementList.stream()
                    .filter(e -> e.account.isFavorite)
                    .collect(Collectors.toList());

            if (!favouriteAlts.isEmpty()) {
                IFont.draw(IFont.MONTSERRAT_MEDIUM, (Utils.isRussianLanguage ? "Избранное" : "Favourite"), accountsRect.getX() + 5, favoriteY, Color.ORANGE, 8);

                favoriteY += IFont.getHeight(IFont.MONTSERRAT_MEDIUM, (Utils.isRussianLanguage ? "Избранное" : "Favourite"), 8) + 2;

                favouriteAlts.sort(Comparator.comparing(e -> e.account.name));

                for (AltElement altElement : favouriteAlts) {
                    altElement.rect.setY(favoriteY);
                    favoriteY += altElement.rect.getH() + 3;
                    if (altElement.rect.getY2() < accountsRect.getY() || altElement.rect.getY() > accountsRect.getY2())
                        continue;
                    altElement.draw(mouseX, mouseY, 1);
                }
            }

            List<AltElement> nonFavouriteAlts = elementList.stream()
                    .filter(e -> !e.account.isFavorite)
                    .collect(Collectors.toList());

            if (!nonFavouriteAlts.isEmpty()) {
                IFont.draw(IFont.MONTSERRAT_MEDIUM, (Utils.isRussianLanguage ? "Остальное" : "Other"), accountsRect.getX() + 5, favoriteY + (favouriteAlts.isEmpty() ? 0 : 7.5f), new Color(162, 162, 162).brighter(), 8);

                float nonFavoriteY = favoriteY + (favouriteAlts.isEmpty() ? 0 : 7.5f) + IFont.getHeight(IFont.MONTSERRAT_MEDIUM, (Utils.isRussianLanguage ? "Остальное" : "Other"), 8) + 2;

                nonFavouriteAlts.sort(Comparator.comparing(e -> e.account.name));

                for (AltElement altElement : nonFavouriteAlts) {
                    altElement.rect.setY(nonFavoriteY);
                    nonFavoriteY += altElement.rect.getH() + 3;
                    if (altElement.rect.getY2() < accountsRect.getY() || altElement.rect.getY() > accountsRect.getY2())
                        continue;
                    altElement.draw(mouseX, mouseY, 1);
                }
            }

            ScissorUtils.unset();
            ScissorUtils.pop();
        });

        super.render(matrices, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        double h = BloodyClient.mc.mouse.getY() / 2;

        if (randButtonRect.intersect(BloodyClient.mc.mouse.getX() / 2, BloodyClient.mc.mouse.getY() / 2)) Accounts.add(AccountUtils.generateRandomAccount(), false);
        if (addButtonRect.intersect(BloodyClient.mc.mouse.getX() / 2, BloodyClient.mc.mouse.getY() / 2) && isSelected) {
            addAccount();
        } else if (isSelected) isSelected = false;
        if (inputRect.intersect(BloodyClient.mc.mouse.getX() / 2, BloodyClient.mc.mouse.getY() / 2)) {
            isSelected = true;
            currentNickname = "";
        }

        elementList.forEach(e -> {
            if (e.rect.getY2() > accountsRect.getY() && e.rect.getY() < accountsRect.getY2() && h > accountsRect.getY() && h < accountsRect.getY2()) e.click(BloodyClient.mc.mouse.getX() / 2, BloodyClient.mc.mouse.getY() / 2, button);
        });

        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if ((accountsRect.intersect(BloodyClient.mc.mouse.getX() / 2, BloodyClient.mc.mouse.getY() / 2) || new FloatRect(sliderRect.getX(), innerRect.getY() + 5, 4, innerRect.getH() - 10).intersect(BloodyClient.mc.mouse.getX() / 2, BloodyClient.mc.mouse.getY() / 2)) && getScrollerHeight() > accountsRect.getH()) {
            this.amount = (float) amount;
        }

        elementList.forEach(e -> e.scroll(BloodyClient.mc.mouse.getX() / 2, BloodyClient.mc.mouse.getY() / 2, amount));

        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            onClose();
            return false;
        }

        if (isSelected) {
            if (keyCode == GLFW.GLFW_KEY_ENTER) {
                addAccount();
            } else if (keyCode == GLFW.GLFW_KEY_BACKSPACE && !currentNickname.isEmpty()) {
                currentNickname = getStringIgnoreLastChar(currentNickname);
            }

            return false;
        }

        elementList.forEach(e -> e.key(keyCode));

        return false;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (isSelected && currentNickname.length() < 16) {
            if (isLatinLetter(chr) || Character.isDigit(chr)) currentNickname += chr;
            else {
                currentError = (Utils.isRussianLanguage ? "Используй символы a-z, A-Z, 0-9 и _" : "Use only a-z, A-Z, 0-9, and _ symbols");
                isError = true;
                error = System.currentTimeMillis();
            }
        }

        elementList.forEach(e -> e.symbol(chr));

        return false;
    }

    @Override
    public void onClose() {
        if (FunctionManager.get(ClickGui.class).clientSound.get()) {
            CustomSoundInstance customSoundInstance = new CustomSoundInstance(SoundManager.BUBBLE_EVENT, SoundCategory.MASTER);
            customSoundInstance.setVolume(FunctionManager.get(ClickGui.class).volume.floatValue());
            mc.getSoundManager().play(customSoundInstance);
        }

        closing = true;
        postTask = () -> mc.openScreen(ShaderScreen.getInstance());
    }

    public void initka() {
        elementList.clear();
        oldWidth = mc.getWindow().getWidth();
        oldHeight = mc.getWindow().getHeight();

        mainRect = new FloatRect(mc.getWindow().getWidth() / 4 - 150 - 6, mc.getWindow().getHeight() / 4 - 125 - 6, 300 + 6 * 2, 290 + 6);

        inputRect = new FloatRect(mc.getWindow().getWidth() / 4 - 150, mc.getWindow().getHeight() / 4 - 125, 230, 20);

        updateRects();

        updateAccounts();
    }

    public void updateRects() {
        addButtonRect = new FloatRect(inputRect.getX2() + 5, inputRect.getY(), 30, 20);
        randButtonRect = new FloatRect(addButtonRect.getX2() + 5, inputRect.getY(), 30, 20);
        innerRect = new FloatRect(inputRect.getX(), inputRect.getY2() + 5, 300, 260);
        accountsRect = new FloatRect(innerRect.getX() + 5, innerRect.getY() + 5, innerRect.getW() - 20, innerRect.getH() - 10);
        sliderRect = new FloatRect(accountsRect.getX2() + 7.5 - 2, innerRect.getY() + 5, 4, innerRect.getH() - 10);
    }
}