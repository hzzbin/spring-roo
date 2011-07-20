package org.springframework.roo.addon.layers.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.roo.addon.layers.service.ServiceLayerMethod.FIND_ALL;
import static org.springframework.roo.addon.layers.service.ServiceLayerMethod.FIND_ENTRIES;
import static org.springframework.roo.addon.layers.service.ServiceLayerMethod.SAVE;
import static org.springframework.roo.addon.layers.service.ServiceLayerMethod.UPDATE;
import static org.springframework.roo.addon.layers.service.ServiceLayerMethod.valueOf;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.springframework.roo.model.JavaSymbolName;
import org.springframework.roo.model.JavaType;
import org.springframework.roo.project.layers.MemberTypeAdditions;
import org.springframework.roo.support.util.PairList;

/**
 * Unit test of the {@link ServiceLayerMethod} enum
 *
 * @author Andrew Swan
 * @since 1.2
 */
public class ServiceLayerMethodTest {

	// Constants
	private static final JavaType TARGET_ENTITY = new JavaType("com.example.Person");
	private static final String PLURAL = "People";
	
	@Test
	public void testValueOfMethodUsingWrongName() {
		assertNull(valueOf("x", Arrays.<JavaType>asList(), TARGET_ENTITY));
	}

	@Test
	public void testValueOfMethodUsingWrongParameterTypes() {
		assertNull(valueOf(FIND_ALL.getKey(), Arrays.asList(JavaType.BYTE_OBJECT), TARGET_ENTITY));
	}

	@Test
	public void testValueOfMethodUsingCorrectDetails() {
		for (final ServiceLayerMethod method : ServiceLayerMethod.values()) {
			assertEquals(method, valueOf(method.getKey(), method.getParameterTypes(TARGET_ENTITY), TARGET_ENTITY));
		}
	}
	
	@Test
	public void testEachMethodHasSameNumberOfParameterTypesAndNames() {
		for (final ServiceLayerMethod method : ServiceLayerMethod.values()) {
			final List<JavaSymbolName> parameterNames = method.getParameterNames(TARGET_ENTITY);
			final List<JavaType> parameterTypes = method.getParameterTypes(TARGET_ENTITY);
			final PairList<JavaType, JavaSymbolName> parameters = method.getParameters(TARGET_ENTITY);
			assertEquals(parameterTypes.size(), parameterNames.size());
			assertEquals(parameterTypes, parameters.getKeys());
			assertEquals(parameterNames, parameters.getValues());
		}
	}
	
	@Test
	public void testEachMethodHasUniqueParameterNames() {
		for (final ServiceLayerMethod method : ServiceLayerMethod.values()) {
			final List<JavaSymbolName> allParameterNames = method.getParameterNames(TARGET_ENTITY);
			final Set<JavaSymbolName> distinctNames = new HashSet<JavaSymbolName>(allParameterNames);
			assertEquals(allParameterNames.size(), distinctNames.size());
		}
	}
	
	@Test
	public void testEachMethodHasNonNullReturnType() {
		for (final ServiceLayerMethod method : ServiceLayerMethod.values()) {
			assertNotNull(method.getReturnType(TARGET_ENTITY));
		}
	}
	
	@Test
	public void testGetBodyWhenLowerLayerImplementsMethod() {
		final MemberTypeAdditions mockLowerLayerAdditions = mock(MemberTypeAdditions.class);
		when(mockLowerLayerAdditions.getMethodSignature()).thenReturn("foo()");
		for (final ServiceLayerMethod method : ServiceLayerMethod.values()) {
			if (method.isVoid()) {
				assertEquals("foo();", method.getBody(mockLowerLayerAdditions));
			} else {
				assertEquals("return foo();", method.getBody(mockLowerLayerAdditions));
			}
		}
	}
	
	@Test
	public void testGetBodyWhenLowerLayerDoesNotImplementMethod() {
		for (final ServiceLayerMethod method : ServiceLayerMethod.values()) {
			assertEquals("throw new UnsupportedOperationException(\"Implement me!\");", method.getBody(null));
		}
	}
	
	@Test
	public void testGetNameWhenAnnotationHasBlankName() {
		final ServiceAnnotationValues mockAnnotationValues = mock(ServiceAnnotationValues.class);
		for (final ServiceLayerMethod method : ServiceLayerMethod.values()) {
			assertNull(method.getName(mockAnnotationValues, TARGET_ENTITY, "x"));
			assertNull(method.getSymbolName(mockAnnotationValues, TARGET_ENTITY, "x"));
		}
	}
	
	@Test
	public void testGetNameOfFindAllMethodWhenAnnotationHasNonBlankName() {
		final ServiceAnnotationValues mockAnnotationValues = mock(ServiceAnnotationValues.class);
		when(mockAnnotationValues.getFindAllMethod()).thenReturn("getAll");
		assertEquals("getAllPeople", FIND_ALL.getName(mockAnnotationValues, TARGET_ENTITY, PLURAL));
		assertEquals("getAllPeople", FIND_ALL.getSymbolName(mockAnnotationValues, TARGET_ENTITY, PLURAL).getSymbolName());
	}
	
	@Test
	public void testGetNameOfFindEntriesMethodWhenAnnotationHasNonBlankName() {
		final ServiceAnnotationValues mockAnnotationValues = mock(ServiceAnnotationValues.class);
		when(mockAnnotationValues.getFindEntriesMethod()).thenReturn("get");
		assertEquals("getPersonEntries", FIND_ENTRIES.getName(mockAnnotationValues, TARGET_ENTITY, PLURAL));
	}
	
	@Test
	public void testGetNameOfSaveMethodWhenAnnotationHasNonBlankName() {
		final ServiceAnnotationValues mockAnnotationValues = mock(ServiceAnnotationValues.class);
		when(mockAnnotationValues.getSaveMethod()).thenReturn("store");
		assertEquals("storePerson", SAVE.getName(mockAnnotationValues, TARGET_ENTITY, PLURAL));
	}
	
	@Test
	public void testGetNameOfUpdateMethodWhenAnnotationHasNonBlankName() {
		final ServiceAnnotationValues mockAnnotationValues = mock(ServiceAnnotationValues.class);
		when(mockAnnotationValues.getUpdateMethod()).thenReturn("change");
		assertEquals("changePerson", UPDATE.getName(mockAnnotationValues, TARGET_ENTITY, PLURAL));
	}
}
