package com.soarclient.gui.modmenu.component;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import com.soarclient.Soar;
import com.soarclient.animation.SimpleAnimation;
import com.soarclient.management.color.api.ColorPalette;
import com.soarclient.management.mod.impl.settings.SystemSettings;
import com.soarclient.management.music.Music;
import com.soarclient.management.music.MusicManager;
import com.soarclient.management.music.ytdlp.Ytdlp;
import com.soarclient.skia.Skia;
import com.soarclient.skia.font.Fonts;
import com.soarclient.skia.font.Icon;
import com.soarclient.ui.component.Component;
import com.soarclient.ui.component.handler.impl.ButtonHandler;
import com.soarclient.ui.component.impl.IconButton;
import com.soarclient.ui.component.impl.text.TextField;
import com.soarclient.utils.Multithreading;
import com.soarclient.utils.mouse.MouseUtils;

public class MusicControlBar extends Component {
	private final List<Component> components = new ArrayList<>();
	private final List<ControlButton> buttons = new ArrayList<>();
	private final SimpleAnimation animation = new SimpleAnimation();
	private boolean addMusic;
	private boolean isDraggingVolume;
	private float lastVolume = 1.0f;

	private final TextField urlField;
	private final IconButton downloadButton;

	public MusicControlBar(float x, float y, float width) {
		super(x, y);
		this.width = width;
		this.height = 64;

		MusicManager musicManager = Soar.getInstance().getMusicManager();

		float offsetY = 26;

		buttons.add(new ControlButton(Icon.REPEAT, 0, y + offsetY, () -> {
			musicManager.setShuffle(false);
			musicManager.setRepeat(!musicManager.isRepeat());
		}));
		buttons.add(new ControlButton(Icon.SKIP_PREVIOUS, 0, y + offsetY, musicManager::back));
		buttons.add(new ControlButton(musicManager.isPlaying() ? Icon.PAUSE : Icon.PLAY_ARROW, 0, y + offsetY, musicManager::switchPlayBack));
		buttons.add(new ControlButton(Icon.SKIP_NEXT, 0, y + offsetY, musicManager::next));
		buttons.add(new ControlButton(Icon.SHUFFLE, 0, y + offsetY, () -> {
			musicManager.setRepeat(false);
			musicManager.setShuffle(!musicManager.isShuffle());
		}));

		float totalWidth = (buttons.size() * 22) + ((buttons.size() - 1) * 2);
		float centerX = x + (width / 2);
		float offsetX = centerX - (totalWidth / 2);

		for (ControlButton b : buttons) {
			b.setX(offsetX);
			offsetX += 30;
		}

		urlField = new TextField(x + 8, y + 12, 320, "");
		downloadButton = new IconButton(Icon.DOWNLOAD, x + 320 + 16, y + 12, IconButton.Size.SMALL, IconButton.Style.PRIMARY);

		centerX = centerX - ((downloadButton.getWidth() + urlField.getWidth() + 8) / 2);
		urlField.setX(centerX);
		downloadButton.setX(centerX + urlField.getWidth() + 8);

		downloadButton.setHandler(new ButtonHandler() {
			@Override
			public void onAction() {
				if (urlField.getText().isEmpty()) {
					return;
				}

				Ytdlp ytdlp = new Ytdlp();
				ytdlp.setFFmpegPath(SystemSettings.getInstance().getFFmpegPath());
				ytdlp.setYtdlpPath(SystemSettings.getInstance().getYtdlpPath());

				Multithreading.runAsync(() -> {
					try {
						boolean result = ytdlp.download(urlField.getText());
						if (result) {
							Soar.getInstance().getMusicManager().load();
						}
					} catch (Exception e) {
						System.err.println("Failed to download music: " + e.getMessage());
					} finally {
						addMusic = false;
					}
				});
			}
		});

		components.add(urlField);
		components.add(downloadButton);
	}

	@Override
	public void draw(double mouseX, double mouseY) {

		Soar instance = Soar.getInstance();
		ColorPalette palette = instance.getColorManager().getPalette();
		MusicManager musicManager = instance.getMusicManager();
		Music music = musicManager.getCurrentMusic();

		animation.onTick(addMusic ? 1 : 0, 16);

		Skia.drawRoundedRect(x, y, width, height, 16, palette.getSurface());

		Skia.save();
		Skia.clip(x, y, width, height, 16);

		Skia.save();
		Skia.translate(0, animation.getValue() * -height);

		File album = music != null ? music.getAlbum() : null;

		if (album != null) {
			Skia.drawRoundedImage(album, x + 8, y + 8, 48, 48, 10);
		} else {
			Skia.drawRoundedRect(x + 8, y + 8, 48, 48, 10, palette.getSurfaceContainerHigh());
		}

		if (music != null) {

			String limitedTitle = Skia.getLimitText(music.getTitle(), Fonts.getRegular(16), 170);
			String limitedArtist = Skia.getLimitText(music.getArtist(), Fonts.getRegular(12), 170);

			Skia.drawText(limitedTitle, x + 66, y + 17, palette.getOnSurface(), Fonts.getRegular(16));
			Skia.drawText(limitedArtist, x + 66, y + 34, palette.getOnSurfaceVariant(), Fonts.getRegular(12));
		}

		float seekWidth = 308;
		float seekHeight = 6;

		drawSeekBar(x + (width / 2) - (seekWidth / 2), y + height - seekHeight - 12, seekWidth, seekHeight);

		// 绘制音量控制条
		float volumeControlWidth = 80;  // 减小音量条宽度
		float volumeX = x + width - volumeControlWidth - 30;  // 调整位置，更靠右
		float volumeY = y + height - seekHeight - 12;

		// 绘制音量图标
		String volumeIcon = musicManager.getVolume() <= 0 ? Icon.VOLUME_OFF :
						   musicManager.getVolume() < 0.5f ? Icon.VOLUME_DOWN : Icon.VOLUME_UP;
		Skia.drawText(volumeIcon, volumeX - 20, volumeY + 5, palette.getOnSurface(), Fonts.getIconFill(14));

		// 绘制音量控制条背景
		Skia.drawRoundedRect(volumeX, volumeY, volumeControlWidth, seekHeight, 3.5F, palette.getSurfaceContainerHigh());

		// 绘制音量控制条进度
		float volumeProgress = musicManager.getVolume() * volumeControlWidth;
		if (volumeProgress > 0) {  // 只在有音量时绘制进度条
			Skia.drawRoundedRect(volumeX, volumeY, volumeProgress, seekHeight, 3.5F, palette.getPrimary());
		}

		buttons.get(2).icon = musicManager.isPlaying() ? Icon.PAUSE : Icon.PLAY_ARROW;
		buttons.get(0).color = musicManager.isRepeat() ? palette.getPrimary() : palette.getOnSurface();
		buttons.get(4).color = musicManager.isShuffle() ? palette.getPrimary() : palette.getOnSurface();

		for (ControlButton b : buttons) {
			b.draw(mouseX, mouseY);
		}

		Skia.restore();

		Skia.save();
		Skia.translate(0, (1 - animation.getValue()) * height);

		for (Component c : components) {
			c.draw(mouseX, mouseY);
		}

		Skia.restore();
		Skia.restore();

		String icon = addMusic ? Icon.CLOSE : Icon.DOWNLOAD;
		float iconWidth = Skia.getTextBounds(icon, Fonts.getRegular(24)).getWidth();

		Skia.drawText(icon, x + width - iconWidth - 8, y + 8, palette.getOnSurface(), Fonts.getIcon(24));
	}

	private void drawSeekBar(float x, float y, float width, float height) {

		Soar instance = Soar.getInstance();
		MusicManager musicManager = instance.getMusicManager();
		ColorPalette palette = instance.getColorManager().getPalette();

		float current = musicManager.getCurrentTime();
		float end = musicManager.getEndTime();

		Skia.drawRoundedRect(x, y, width, height, 3.5F, palette.getSurfaceContainerHigh());

		if (end > 0 && current >= 0 && current <= end && !Float.isInfinite(end) && !Float.isNaN(end)) {
			float progress = (current / end) * width;
			progress = Math.min(progress, width);
			Skia.drawRoundedRect(x, y, progress, height, 3.5F, palette.getPrimary());
		}
	}

	@Override
	public void mousePressed(double mouseX, double mouseY, int button) {
		if (addMusic) {
			for (Component c : components) {
				c.mousePressed(mouseX, mouseY, button);
			}
		} else {
			// 检查音量控制区域的点击
			float volumeControlWidth = 80;  // 修改为与渲染时相同的宽度
			float volumeX = x + width - volumeControlWidth - 30;  // 修改为与渲染时相同的位置
			float volumeY = y + height - 6 - 12;

			// 检查音量图标点击（静音/取消静音）
			if (MouseUtils.isInside(mouseX, mouseY, volumeX - 30, volumeY - 6, 24, 24)
					&& button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
				MusicManager musicManager = Soar.getInstance().getMusicManager();
				if (musicManager.getVolume() > 0) {
					lastVolume = musicManager.getVolume();
					musicManager.setVolume(0);
				} else {
					musicManager.setVolume(lastVolume);
				}
				return;
			}

			// 检查音量控制条点击
			if (MouseUtils.isInside(mouseX, mouseY, volumeX - 2, volumeY - 4, volumeControlWidth + 4, 12)
					&& button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
				isDraggingVolume = true;
				updateVolumeFromMouse(mouseX, volumeX, volumeControlWidth);
				return;
			}

			for (ControlButton b : buttons) {
				b.mousePressed(mouseX, mouseY, button);
			}
		}
	}

	private void updateVolumeFromMouse(double mouseX, float volumeX, float volumeControlWidth) {
		// 将鼠标位置限制在音量条范围内
		mouseX = Math.max(volumeX, Math.min(volumeX + volumeControlWidth, mouseX));
		float volume = (float) ((mouseX - volumeX) / volumeControlWidth);
		Soar.getInstance().getMusicManager().setVolume(volume);
	}

	public void mouseDragged(int button, double mouseX, double mouseY, double deltaX, double deltaY) {
		if (!addMusic && isDraggingVolume) {
			float volumeControlWidth = 80;
			float volumeX = x + width - volumeControlWidth - 30;
			updateVolumeFromMouse(mouseX, volumeX, volumeControlWidth);
		}
	}

	@Override
	public void mouseReleased(double mouseX, double mouseY, int button) {
		isDraggingVolume = false;

		if (addMusic) {
			for (Component c : components) {
				c.mouseReleased(mouseX, mouseY, button);
			}
		} else {
			for (ControlButton b : buttons) {
				b.mouseReleased(mouseX, mouseY, button);
			}
		}

		String icon = addMusic ? Icon.CLOSE : Icon.DOWNLOAD;
		float iconWidth = Skia.getTextBounds(icon, Fonts.getRegular(24)).getWidth();

		if (MouseUtils.isInside(mouseX, mouseY, x + width - iconWidth - 16, y, 32, 32)
				&& button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			addMusic = !addMusic;
		}
	}

	@Override
	public void keyPressed(int keyCode, int scanCode, int modifiers) {
		if (addMusic) {
			for (Component c : components) {
				c.keyPressed(keyCode, scanCode, modifiers);
			}
		}
	}

	@Override
	public void charTyped(char chr, int modifiers) {
		if (addMusic) {
			for (Component c : components) {
				c.charTyped(chr, modifiers);
			}
		}
	}

	private static class ControlButton extends Component {
		private String icon;
		private final Runnable task;
		private Color color;

		public ControlButton(String icon, float x, float y, Runnable task) {
			super(x, y);
			this.icon = icon;
			this.width = 26;
			this.height = 26;
			this.task = task;
			this.color = Soar.getInstance().getColorManager().getPalette().getOnSurface();
		}

		@Override
		public void draw(double mouseX, double mouseY) {
			Skia.drawFullCenteredText(icon, x, y, color, Fonts.getIconFill(28));
		}

		@Override
		public void mouseReleased(double mouseX, double mouseY, int button) {
			if (MouseUtils.isInside(mouseX, mouseY, x - 13, y - 13, 28, 28)
					&& button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
				task.run();
			}
		}

		public void setColor(Color color) {
			this.color = color;
		}
	}
}
