/*
 * SPDX-FileCopyrightText: 2023-2026 Toshiaki Maki <makingx@gmail.com>
 * SPDX-License-Identifier: Apache-2.0
 */
package am.ik.json;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(packages = "am.ik.json", importOptions = ImportOption.DoNotIncludeTests.class)
class PackageCycleArchTest {

	// The library is a single package today, so there is no slice to check yet.
	@ArchTest
	static final ArchRule packagesAreFreeOfCycles = slices().matching("am.ik.json.(*)..")
		.should()
		.beFreeOfCycles()
		.allowEmptyShould(true);

}
