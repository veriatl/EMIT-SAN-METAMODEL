package fr.icam.emit.san.metamodel.core

import org.eclipse.emf.common.util.EList
import org.eclipse.emf.ecore.EObject

class SAN2Script {
  

	/* 
	 * Entry point of model to Boogie transformation
	 * */ 
	def mapEObjects(EList<EObject> eobjects) '''	
		«FOR eobject: eobjects.filter(typeof(EObject))»		
			«FOR sf : eobject.eClass.EAllStructuralFeatures»
				«eobject.eGet(sf)»
			«ENDFOR»
		«ENDFOR»
	'''

	
}
