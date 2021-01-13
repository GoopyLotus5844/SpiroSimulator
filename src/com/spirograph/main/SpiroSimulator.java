package com.spirograph.main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;

import com.spirograph.design.DrawAnimationManager;

public class SpiroSimulator extends Canvas implements Runnable{

	private static final long serialVersionUID = 1L; 
	private static Dimension resolution = Toolkit.getDefaultToolkit().getScreenSize();
	public static final int WIDTH = (int) resolution.getWidth(), HEIGHT = (int) resolution.getHeight() - 100;
	public static final Point CENTER_POINT = new Point(WIDTH / 2, HEIGHT / 2);
	private boolean running = false;
	
	private Thread thread;
	private Window window;
	private SpiroButtonPanel buttonPanel;
	private DrawAnimationManager animationManager;
	
	public SpiroSimulator() {
		window = new Window(WIDTH, HEIGHT, "SpiroSimulator");
		animationManager = new DrawAnimationManager(this);
		window.open(this);
		start();
	}
	
	public synchronized void start() {
		thread = new Thread(this);
		thread.start();
		running = true;
	}
	
	public synchronized void stop() {
		try {
			thread.join();
			running = false;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		
		buttonPanel = new SpiroButtonPanel(this);
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		long timer = System.currentTimeMillis();
		int updates = 0;
		int frames = 0;
		while(running) {	
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while(delta >= 1){
				tick();
				updates++;
				delta--;
			}	
			render();
			frames++;				
			if(System.currentTimeMillis() - timer > 1000){
				timer += 1000;
				System.out.println("FPS: " + frames + " TICKS: " + updates);
				frames = 0;
				updates = 0;
			}
		}
		stop();
	}
	
	private void tick() {
		animationManager.tick();
	}
	
	private void render(){
		BufferStrategy bs = this.getBufferStrategy();
		if(bs == null){
			this.createBufferStrategy(2);
			return;
		}
		
		Graphics g = bs.getDrawGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		buttonPanel.render(g);
		animationManager.render(g);
		g.dispose();
		bs.show();
	}
	
	public static float truncate(float f, int decimalDigits) {
		String floatString = Float.toString(f);
		int pointIndex = floatString.indexOf(".");
		floatString = floatString.substring(0, pointIndex + decimalDigits + 1);
		return Float.parseFloat(floatString);
	}
	
	public SpiroButtonPanel getButtonPanel() {
		return buttonPanel;
	}
	
	public DrawAnimationManager getAnimationManager() {
		return animationManager;
	}
	
	public static void main(String[] args) {
		new SpiroSimulator();
	}
}
