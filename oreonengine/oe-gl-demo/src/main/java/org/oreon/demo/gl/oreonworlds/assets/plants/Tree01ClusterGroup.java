package org.oreon.demo.gl.oreonworlds.assets.plants;

import org.oreon.core.gl.buffers.GLMeshVBO;
import org.oreon.core.gl.config.CullFaceDisable;
import org.oreon.core.gl.config.Default;
import org.oreon.core.gl.util.modelLoader.obj.OBJLoader;
import org.oreon.core.instancing.InstancedDataObject;
import org.oreon.core.instancing.InstancingCluster;
import org.oreon.core.instancing.InstancingObject;
import org.oreon.core.instancing.InstancingObjectHandler;
import org.oreon.core.math.Vec3f;
import org.oreon.core.model.Model;
import org.oreon.core.model.Vertex;
import org.oreon.core.renderer.RenderInfo;
import org.oreon.core.system.CoreSystem;
import org.oreon.core.util.Util;
import org.oreon.demo.gl.oreonworlds.shaders.assets.plants.TreeBillboardShader;
import org.oreon.demo.gl.oreonworlds.shaders.assets.plants.TreeBillboardShadowShader;
import org.oreon.demo.gl.oreonworlds.shaders.assets.plants.TreeLeavesShader;
import org.oreon.demo.gl.oreonworlds.shaders.assets.plants.TreeShadowShader;
import org.oreon.demo.gl.oreonworlds.shaders.assets.plants.TreeTrunkShader;

public class Tree01ClusterGroup extends InstancingObject{
	
	public Tree01ClusterGroup(){
		
		Model[] models = new OBJLoader().load("oreonworlds/assets/plants/Tree_01","tree01.obj","tree01.mtl");
		Model[] billboards = new OBJLoader().load("oreonworlds/assets/plants/Tree_01","billboardmodel.obj","billboardmodel.mtl");
		
		for (Model model : models){
			
			InstancedDataObject object = new InstancedDataObject();
			GLMeshVBO meshBuffer = new GLMeshVBO();
			
			if (model.equals(models[0])){
				model.getMesh().setTangentSpace(true);
				Util.generateTangentsBitangents(model.getMesh());
			}
			else
				model.getMesh().setTangentSpace(false);
			model.getMesh().setInstanced(true);
			
			for (Vertex vertex : model.getMesh().getVertices()){
				vertex.getPosition().setX(vertex.getPosition().getX()*1.2f);
				vertex.getPosition().setZ(vertex.getPosition().getZ()*1.2f);
			}
			
			meshBuffer.addData(model.getMesh());

			if (model.equals(models[0])){
				object.setRenderInfo(new RenderInfo(new Default(), TreeTrunkShader.getInstance()));
				object.setShadowRenderInfo(new RenderInfo(new Default(), TreeShadowShader.getInstance()));
			}
			else{
				object.setRenderInfo(new RenderInfo(new Default(), TreeLeavesShader.getInstance()));
				object.setShadowRenderInfo(new RenderInfo(new Default(), TreeShadowShader.getInstance()));
			}
			
			object.setMaterial(model.getMaterial());
			object.setVao(meshBuffer);
			getObjectData().add(object);
		}
		
		for (Model billboard : billboards){	
			InstancedDataObject object = new InstancedDataObject();
			GLMeshVBO meshBuffer = new GLMeshVBO();
			
			billboard.getMesh().setTangentSpace(false);
			billboard.getMesh().setInstanced(true);
			
			for (Vertex vertex : billboard.getMesh().getVertices()){
				vertex.setPosition(vertex.getPosition().mul(7.4f));
				vertex.getPosition().setX(vertex.getPosition().getX()*1f);
				vertex.getPosition().setZ(vertex.getPosition().getZ()*1f);
			}
			
			meshBuffer.addData(billboard.getMesh());
	
			object.setRenderInfo(new RenderInfo(new CullFaceDisable(), TreeBillboardShader.getInstance()));
			object.setShadowRenderInfo(new RenderInfo(new CullFaceDisable(), TreeBillboardShadowShader.getInstance()));
			
			object.setMaterial(billboard.getMaterial());
			object.setVao(meshBuffer);
			getObjectData().add(object);
		}
	
		addCluster(new Tree01Cluster(10,new Vec3f(-1002,0,1550),getObjectData()));
		addCluster(new Tree01Cluster(10,new Vec3f(-1085,0,1536),getObjectData()));
		addCluster(new Tree01Cluster(10,new Vec3f(-1121,0,1473),getObjectData()));
		addCluster(new Tree01Cluster(10,new Vec3f(-1114,0,1423),getObjectData()));
		addCluster(new Tree01Cluster(10,new Vec3f(-1074,0,1378),getObjectData()));
		addCluster(new Tree01Cluster(10,new Vec3f(-1138,0,1345),getObjectData()));
		addCluster(new Tree01Cluster(10,new Vec3f(-1039,0,1129),getObjectData()));
		addCluster(new Tree01Cluster(10,new Vec3f(-1011,0,1042),getObjectData()));
		addCluster(new Tree01Cluster(6,new Vec3f(-1181,0,1346),getObjectData()));
		addCluster(new Tree01Cluster(6,new Vec3f(-1210,0,1348),getObjectData()));
		addCluster(new Tree01Cluster(6,new Vec3f(-1211,0,1392),getObjectData()));
		
		setThread(new Thread(this));
		getThread().start();
	}

	public void run() {
		while(isRunning()){
			
			InstancingObjectHandler.getInstance().getLock().lock();
			try {
				InstancingObjectHandler.getInstance().getCondition().await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			finally{
				InstancingObjectHandler.getInstance().getLock().unlock();
			}
			
			synchronized (getChildren()) {
				
				getChildren().clear();
				
				for (InstancingCluster cluster : getClusters()){
					if (cluster.getCenter().sub(CoreSystem.getInstance().getScenegraph().getCamera().getPosition()).length() < 2000){
						cluster.updateUBOs();
						addChild(cluster);
					}
				}
				
			}
		}
	}
}
