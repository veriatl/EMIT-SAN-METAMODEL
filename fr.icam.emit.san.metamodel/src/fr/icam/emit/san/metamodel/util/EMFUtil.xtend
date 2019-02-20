package fr.icam.emit.san.metamodel.util

import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.emf.ecore.util.BasicExtendedMetaData
import org.eclipse.emf.ecore.util.ExtendedMetaData
import org.eclipse.emf.ecore.xmi.XMLResource
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl

class EMFUtil {
	
	/** 
	 * @return the first epackage from the resource.
	 * */	
	def static getEPackage(Resource resource) {
		for (content : resource.contents) {
			if (content instanceof EPackage) {
				return content
			}
		}
	}
	
	/**
	 * load ecore in memory from {@code path}.
	 * @return resource set that have ecore metamodel loaded
	 * */
	def static loadEcore(URI path){
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
		Resource.Factory.Registry.INSTANCE.extensionToFactoryMap.put("xmi", new XMIResourceFactoryImpl());
		
		val ResourceSet rs = new ResourceSetImpl();
		val ExtendedMetaData extendedMetaData = new BasicExtendedMetaData(rs.getPackageRegistry());
		rs.getLoadOptions().put(XMLResource.OPTION_EXTENDED_META_DATA, extendedMetaData);


		val Resource r = rs.getResource(path, true);
		val EObject eObject = r.getContents().get(0);
		if (eObject instanceof EPackage) {
    		val EPackage p = eObject as EPackage;
    		rs.getPackageRegistry().put(p.getNsURI(), p);
    		return rs
		}else{
			throw new Exception("metamodel does not found at"+path)
		}
	}
	
	
}
