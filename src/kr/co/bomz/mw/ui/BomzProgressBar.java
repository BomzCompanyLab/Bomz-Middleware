package kr.co.bomz.mw.ui;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import kr.co.bomz.mw.service.SceneLoaderService;

public class BomzProgressBar extends Stage{

	private Popup popup = new Popup();
	private Label stateLb = new Label("init...");
	private ProgressBar bar = new ProgressBar();
	
	private Queue<Boolean> stepQueue = new LinkedBlockingQueue<Boolean>();
	private Queue<String> messageQueue = new LinkedBlockingQueue<String>();
	
	/**
	 * 		���α׷����� ó�� �ܰ�. �� �ܰ�� �̷������� ����
	 * 		0 �� ��� �ڵ� ó��
	 */
	private final int maxStep;
	
	private Task<Void> task;
	
	public BomzProgressBar(String title){
		this(0);
	}
	
	public BomzProgressBar(int maxStep){
		super(StageStyle.TRANSPARENT);
		super.initModality(Modality.APPLICATION_MODAL);
		super.setResizable(false);
		super.initOwner(SceneLoaderService.getInstance().getStage());
		
		this.maxStep = maxStep;			// 0���� �۰ų� ���� ��� 0���� ó��
		
		this.initLayout();
	}
	
	public void showProgress(){
		if( this.task != null )		this.hideProgress();
		
		this.task = new Task<Void>(){
			@Override
			protected Void call() throws Exception {
				int nowProgress = 0;
				while( !isCancelled() ){
					
					// Step ����
					if( maxStep <= 0 ){		// �ڵ������� ���
						nowProgress++;
						if( nowProgress > 100 )		nowProgress = 0;
						updateProgress(nowProgress, 100);
					}else{		// �ڵ� ������ �ƴ� ���
						if( stepQueue.poll() != null )		updateProgress(++nowProgress, maxStep);
					}

					// �޼��� ����
					if( !messageQueue.isEmpty() )		updateMessage(messageQueue.poll());		
					
					try{		Thread.sleep(100);		}catch(Exception e){}
				}
				
				return null;
			}
		};
		
		this.bar.progressProperty().bind(this.task.progressProperty());
		this.stateLb.textProperty().bind(this.task.messageProperty());
		
		new Thread(this.task).start();
		
		super.show();
		this.popup.show(this);
	}
	
	public void hideProgress(){
		if( this.task != null ){
			this.task.cancel();
			this.task = null;
		}
		
		this.popup.hide();
		super.hide();
	}
	
	/**		ȭ�� ����		*/
	private void initLayout(){
		VBox box = new VBox();
		box.setSpacing(8);
		box.setPadding(new Insets(10));
		box.setPrefWidth(300);
		
		box.getChildren().addAll(
				this.makeCenterPane(box, this.bar), 
				this.makeCenterPane(box, this.stateLb)
			);
		box.getStylesheets().add("kr/co/bomz/cmn/ui/popup/Popup.css");		// css ����
		
		this.popup.getContent().clear();
		this.popup.getContent().add(box);
	}
	
	private FlowPane makeCenterPane(VBox box, Control control){
		FlowPane pn = new FlowPane();
		pn.setAlignment(Pos.CENTER);
		pn.getChildren().add(control);
		
		pn.prefWidthProperty().bind(box.prefWidthProperty());
		
		if( control instanceof ProgressBar )	control.prefWidthProperty().bind(pn.prefWidthProperty());
		
		return pn;
	}

	/**		���α׷����� �� ����		*/
	public void nextProgressStep() {
		if( this.maxStep <= 0 )		return;		// �ڵ������� ��� ó���� �� ����
		this.stepQueue.offer(true);
	}

	/**		���α׷����� �� �� �޼��� ����		*/
	public void nextProgressStep(String progressMessage){
		this.nextProgressStep();
		this.updateProgressMessage(progressMessage);
	}
	
	/**		���α׷����� �޼��� ����		*/
	public void updateProgressMessage(String progressMessage) {
		if( progressMessage == null )		return;
		this.messageQueue.offer(progressMessage);
	}
	
}
